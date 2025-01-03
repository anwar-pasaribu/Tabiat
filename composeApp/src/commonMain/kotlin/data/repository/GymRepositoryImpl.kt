/*
 * MIT License
 *
 * Copyright (c) 2024 Anwar Pasaribu
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Project Name: Tabiat
 */
package data.repository

import data.source.local.dao.IExerciseDao
import data.source.local.dao.IExerciseLogDao
import data.source.local.dao.IWorkoutPlanDao
import data.source.local.dao.IWorkoutPlanExerciseDao
import data.source.preferences.IPreferencesDataSource
import data.source.remote.api.IGymApi
import domain.model.detail.DetailItemEntity
import domain.model.gym.DifficultyLevel
import domain.model.gym.Exercise
import domain.model.gym.ExerciseLog
import domain.model.gym.ExerciseProgress
import domain.model.gym.ExerciseSet
import domain.model.gym.GymPreferences
import domain.model.gym.WorkoutPlan
import domain.model.gym.WorkoutPlanExercise
import domain.model.gym.WorkoutPlanProgress
import domain.model.home.HomeItemEntity
import domain.repository.IGymRepository
import domain.repository.IPersonalizationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GymRepositoryImpl(
    private val personalizationRepository: IPersonalizationRepository,
    private val gymApi: IGymApi,
    private val exerciseDao: IExerciseDao,
    private val workoutPlanDao: IWorkoutPlanDao,
    private val workoutPlanExerciseDao: IWorkoutPlanExerciseDao,
    private val exerciseLogDao: IExerciseLogDao,
    private val preferencesDataSource: IPreferencesDataSource,
) : IGymRepository {

    override suspend fun createWorkoutPlan(workoutName: String, notes: String): Boolean {
        val latestWoPlan = workoutPlanDao.getLatestWorkoutPlan()
        workoutPlanDao.insertWorkoutPlan(
            name = workoutName,
            description = notes,
            datetimeStamp = Clock.System.now().toEpochMilliseconds(),
            orderingNumber = latestWoPlan.orderingNumber + 1,
        )
        return true
    }

    override suspend fun updateWorkoutPlan(
        workoutPlanId: Long,
        workoutName: String,
        notes: String,
    ): Boolean {
        workoutPlanDao.updateWorkoutPlan(
            workoutPlanId = workoutPlanId,
            name = workoutName,
            description = notes,
        )
        return true
    }

    override suspend fun deleteWorkoutPlan(workoutPlanId: Long): Boolean {
        workoutPlanDao.deleteWorkoutPlan(workoutPlanId)
        return true
    }

    override suspend fun getWorkoutPlansObservable(): Flow<List<WorkoutPlan>> {
        return workoutPlanDao.getAllWorkoutPlanObservable()
    }

    override suspend fun getWorkoutPlanExercises(workoutPlanId: Long): List<Exercise> {
        return getExerciseListByWorkoutPlanId(workoutPlanId)
    }

    private suspend fun getExerciseListByWorkoutPlanId(workoutPlanId: Long): List<Exercise> {
        val exercises = mutableListOf<Exercise>()
        workoutPlanExerciseDao.getAllWorkoutPlanExercise(workoutPlanId).forEach { wpe ->
            val exercise = exerciseDao.getExerciseById(wpe.exerciseId)
            if (exercise != null) {
                exercises.add(exercise)
            }
        }
        return exercises.distinctBy { it.id }
    }

    override suspend fun getWorkoutPlanExercisesObservable(workoutPlanId: Long): Flow<List<Exercise>> {
        val workoutPlanExercises =
            workoutPlanExerciseDao.getAllWorkoutPlanExerciseObservable(workoutPlanId)
        return workoutPlanExercises.map { wpeList ->
            wpeList.map {
                exerciseDao.getExerciseById(it.exerciseId)
                    ?: throw Exception("Exercise id: ${it.exerciseId} not found")
            }.distinctBy { it.id }
        }
    }

    override suspend fun getWorkoutPlanExerciseProgress(
        workoutPlanId: Long,
        exerciseId: Long,
    ): ExerciseProgress {
        return getExerciseProgress(workoutPlanId, exerciseId)
    }

    private suspend fun getExerciseProgress(
        workoutPlanId: Long,
        exerciseId: Long,
    ): ExerciseProgress {
        val workoutPlanExerciseList =
            workoutPlanExerciseDao.selectWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
                workoutPlanId = workoutPlanId,
                exerciseId = exerciseId,
            )
        val total = workoutPlanExerciseList.size
        val progressCount = workoutPlanExerciseList.count { it.finishedDateTime != 0L }
        val exercise = exerciseDao.getExerciseById(exerciseId)
            ?: throw Exception("Exercise id: $exerciseId not found")
        return ExerciseProgress(
            exercise = exercise,
            sessionTotal = total,
            sessionDoneCount = progressCount,
        )
    }

    private suspend fun calculateWorkoutPlanProgress(workoutPlan: WorkoutPlan): WorkoutPlanProgress {
        val woExerciseProgressList =
            workoutPlanExerciseDao.getAllWorkoutPlanExercise(
                workoutPlanId = workoutPlan.id,
            ).map { workoutPlanExercise ->
                getExerciseProgress(
                    workoutPlanId = workoutPlanExercise.workoutPlanId,
                    exerciseId = workoutPlanExercise.exerciseId,
                )
            }
        val total = woExerciseProgressList.sumOf { it.sessionTotal }
        val progress = woExerciseProgressList.sumOf { it.sessionDoneCount }
        val latestExerciseLog = exerciseLogDao.getLatestExerciseLog(
            workoutPlanId = workoutPlan.id,
        )
        val lastExercise = latestExerciseLog?.let {
            exerciseDao.getExerciseById(it.exerciseId)
        }
        val workoutPersonalization =
            personalizationRepository.getWorkoutPlanPersonalization(
                workoutPlanId = workoutPlan.id
            )
        return WorkoutPlanProgress(
            workoutPlan = workoutPlan,
            total = total,
            progress = progress,
            lastExerciseLog = latestExerciseLog,
            lastExercise = lastExercise,
            workoutPersonalization = workoutPersonalization
        )
    }

    override fun getPlanProgressListObservable(): Flow<List<HomeItemEntity>> {
        return workoutPlanDao.getAllWorkoutPlanWithProgressObservable()
    }

    override fun getPlanExerciseListObservable(workoutPlanId: Long): Flow<List<DetailItemEntity>> {
        return workoutPlanDao.getAllWorkoutPlanExerciseWithProgressObservable(workoutPlanId = workoutPlanId)
    }

    override fun getWorkoutPlanProgressListObservable(): Flow<List<WorkoutPlanProgress>> {
        return workoutPlanDao
            .getAllWorkoutPlanObservable().flatMapConcat { workoutPlanList ->
                flow {
                    emit(workoutPlanList.map { workoutPlan ->
                        calculateWorkoutPlanProgress(workoutPlan = workoutPlan)
                    })
                }
            }
    }

    override suspend fun deleteWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
        workoutPlanId: Long,
        exerciseId: Long,
    ): Boolean {
        workoutPlanExerciseDao.deleteWorkoutPlanExerciseByWorkoutPlanIdAndExerciseId(
            workoutPlanId = workoutPlanId,
            exerciseId = exerciseId,
        )
        return true
    }

    override suspend fun deleteWorkoutPlanExerciseSet(workoutPlanExerciseId: Long): Boolean {
        workoutPlanExerciseDao
            .deleteWorkoutPlanExerciseSet(workoutPlanExerciseId)
        return true
    }

    override suspend fun getWorkoutPlanById(workoutPlanId: Long): WorkoutPlan {
        return workoutPlanDao.getWorkoutPlan(workoutPlanId)
    }

    override suspend fun logExercise(
        workoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int,
    ): Boolean {
        val finishedDateTime = Clock.System.now().toEpochMilliseconds()

        exerciseLogDao.insertExerciseLog(
            exerciseId = exerciseId,
            workoutPlanId = workoutPlanId,
            reps = reps.toLong(),
            weight = weight.toLong(),
            difficulty = 0L,
            measurement = "kg",
            setNumberOrder = 1L,
            finishedDateTime = finishedDateTime,
            logNotes = "",
            latitude = -1.0,
            longitude = -1.0,
        )

        workoutPlanExerciseDao.updateWorkoutPlanExerciseFinishedDateTime(
            workoutPlanExerciseId = workoutPlanExerciseId,
            finishedDateTime = finishedDateTime,
            reps = reps.toLong(),
            weight = weight.toLong(),
        )

        return true
    }

    override suspend fun updateWorkoutExerciseRepsAndWeight(
        workoutPlanExerciseId: Long,
        reps: Int,
        weight: Int,
    ): Boolean {
        workoutPlanExerciseDao.updateWorkoutPlanExerciseRepsAndWeight(
            workoutPlanExerciseId = workoutPlanExerciseId,
            reps = reps.toLong(),
            weight = weight.toLong(),
        )

        return true
    }

    override suspend fun getExerciseLogListByDateTimeStamp(dateTimeStamp: Long): List<ExerciseLog> {
        val tz = TimeZone.currentSystemDefault()
        val endOfTimeStampOfSelectedDate = LocalTime(hour = 23, minute = 59, second = 59)
        val dateTime = LocalDateTime(
            date = Instant.fromEpochMilliseconds(dateTimeStamp).toLocalDateTime(tz).date,
            time = endOfTimeStampOfSelectedDate,
        )
        val untilTimeStampMillis = dateTime.toInstant(tz).toEpochMilliseconds()

        val selectedDateTime = Instant.fromEpochMilliseconds(
            untilTimeStampMillis,
        ).toLocalDateTime(tz)
        val firstSecondOfTheDay = LocalTime(hour = 0, minute = 0, second = 1)
        val dateTimeTodayMorning =
            LocalDateTime(date = selectedDateTime.date, time = firstSecondOfTheDay)
        val startTimeStampMillis = dateTimeTodayMorning.toInstant(tz).toEpochMilliseconds()
        return exerciseLogDao.getExerciseLogByDateTimeRange(
            startTimeStampMillis,
            untilTimeStampMillis,
        )
    }

    override suspend fun getExerciseLogListByExerciseId(exerciseId: Long): Flow<List<ExerciseLog>> {
        return exerciseLogDao.getExerciseLogByExerciseId(
            exerciseId = exerciseId,
        )
    }

    override fun getGymPreferences(): Flow<GymPreferences> {
        return preferencesDataSource.getGymPreference()
    }

    override suspend fun saveExerciseSetTimerDuration(timerDurationInSeconds: Int) {
        preferencesDataSource.saveExerciseSetTimerDuration(
            timerDurationInSeconds,
        )
    }

    override suspend fun saveBreakTimeDuration(durationInSeconds: Int) {
        preferencesDataSource.saveExerciseBreakTimeDuration(
            durationInSeconds,
        )
    }

    override suspend fun createNewExercise(newExercise: Exercise): Boolean {
        exerciseDao.insertExercise(
            name = newExercise.name,
            difficulty = newExercise.difficulty.toLong(),
            equipment = newExercise.equipment,
            instructions = newExercise.instructions,
            video = newExercise.video,
            image = newExercise.image,
            targetMuscle = Json.encodeToString(listOf(newExercise.targetMuscle)),
            description = newExercise.description,
            type = 0L,
        )
        return true
    }

    override suspend fun getExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long,
    ): List<WorkoutPlanExercise> {
        return workoutPlanExerciseDao.getAllWorkoutPlanExercise(workoutPlanId).filter {
            it.exerciseId == exerciseId
        }
    }

    override suspend fun getExerciseById(exerciseId: Long): Exercise {
        return exerciseDao.getExerciseById(exerciseId)
            ?: throw Exception("Exercise id: $exerciseId not found")
    }

    override suspend fun insertExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long,
        exerciseSetList: List<ExerciseSet>,
    ): Boolean {
        exerciseSetList.forEach { exerciseSet ->
            workoutPlanExerciseDao.insertWorkoutPlanExercise(
                exerciseId = exerciseId,
                workoutPlanId = workoutPlanId,
                reps = exerciseSet.reps.toLong(),
                weight = exerciseSet.weight.toLong(),
                setNumberOrder = exerciseSet.setNumber.toLong(),
                finishedDateTime = 0L,
            )
        }
        return true
    }

    override suspend fun getWorkoutPlans(): List<WorkoutPlan> {
        return workoutPlanDao.getAllWorkoutPlan()
    }

    override suspend fun resetAllYesterdayActivities() {
        val lastResetTime = preferencesDataSource.getLastExerciseResetTimeStamp()
        if (!isToday(lastResetTime)) {
            resetLastDayWorkoutPlanExerciseList()
        }
    }

    override suspend fun getExerciseCategories(): List<String> {
        return exerciseDao.getAllExerciseCategories()
    }

    override fun getRunningTimerDuration(): Flow<Int> {
        return preferencesDataSource.getRunningTimerDurationTimeStamp()
    }

    override suspend fun saveRunningTimerDuration(duration: Int) {
        var timer = duration
        while (timer >= 0) {
            preferencesDataSource.saveRunningTimerDuration(timer)
            delay(1000)
            timer = timer - 1
        }
    }

    override suspend fun getAllExercisesObservable(): Flow<List<Exercise>> {
        val exerciseCount = exerciseDao.exerciseListCount()
        if (exerciseCount == 0L) {
            val succeed = loadRemoteExercises()
            if (!succeed) {
                return flow {
                    throw Exception("")
                }
            }
        }
        return exerciseDao.getAllExercisesObservable()
    }

    override suspend fun filterExercisesByTargetMuscle(targetMuscle: String): List<Exercise> {
        return exerciseDao.filterExercisesByTargetMuscle(targetMuscle)
    }

    private suspend fun resetLastDayWorkoutPlanExerciseList() = coroutineScope {
        withContext(Dispatchers.Default) {
            workoutPlanDao.getAllWorkoutPlan().forEach { workoutPlan ->
                workoutPlanExerciseDao.getAllWorkoutPlanExercise(workoutPlan.id)
                    .forEach { workoutPlanExercise ->
                        val isYesterday = isOldExercise(workoutPlanExercise.finishedDateTime)
                        if (isYesterday) {
                            workoutPlanExerciseDao.updateWorkoutPlanExerciseFinishedDateTime(
                                workoutPlanExerciseId = workoutPlanExercise.id,
                                finishedDateTime = 0L,
                                reps = workoutPlanExercise.reps.toLong(),
                                weight = workoutPlanExercise.weight.toLong(),
                            )
                        }
                    }
                preferencesDataSource.saveLastExerciseReset(
                    Clock.System.now().toEpochMilliseconds(),
                )
            }
        }
    }

    private fun isOldExercise(timestamp: Long): Boolean {
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz).date
        val timestampDate = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(tz).date

        return today != timestampDate
    }

    private fun isToday(timestamp: Long): Boolean {
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz).date
        val timestampDate = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(tz).date

        return today == timestampDate
    }

    private suspend fun loadRemoteExercises(): Boolean = coroutineScope {
        try {
            withContext(Dispatchers.IO) {
                val networkExercises = gymApi.loadExerciseList()
                networkExercises.forEach { networkExercise ->

                    launch {
                        val difficultyLevel = when (networkExercise.level) {
                            "beginner" -> DifficultyLevel.BEGINNER.level
                            "intermediate" -> DifficultyLevel.INTERMEDIATE.level
                            "expert" -> DifficultyLevel.EXPERT.level
                            else -> {
                                0L
                            }
                        }

                        val description = """
                        Category: ${networkExercise.category}
                        Mechanics: ${networkExercise.mechanic}
                        Force: ${networkExercise.force}
                        """.trimIndent()

                        exerciseDao.insertExercise(
                            name = networkExercise.name.orEmpty(),
                            difficulty = difficultyLevel,
                            equipment = networkExercise.equipment.orEmpty(),
                            instructions = Json.encodeToString(networkExercise.instructions),
                            video = "",
                            image = Json.encodeToString(networkExercise.images),
                            targetMuscle = Json.encodeToString(
                                networkExercise.primaryMuscles.orEmpty() + networkExercise.secondaryMuscles.orEmpty(),
                            ),
                            description = description,
                            type = 0L,
                        )
                    }
                }
            }
            return@coroutineScope true
        } catch (e: Exception) {
            return@coroutineScope false
        }
    }

    override suspend fun searchExercises(searchQuery: String): List<Exercise> {
        return exerciseDao.searchExercises(searchQuery)
    }

    override suspend fun deleteAllExerciseData() {
        withContext(Dispatchers.IO) {
            exerciseDao.deleteAllExerciseData()
        }
    }

    override suspend fun saveTimerSoundType(soundTypeCode: Int) {
        preferencesDataSource.setTimerSoundType(soundTypeCode)
    }

    private fun LocalDate.toEpochTimeStamp(): Long {
        val tz = TimeZone.currentSystemDefault()
        val endOfTimeStampOfSelectedDate = LocalTime(hour = 23, minute = 59, second = 59)
        val dateTime = LocalDateTime(date = this, time = endOfTimeStampOfSelectedDate)
        return dateTime.toInstant(tz).toEpochMilliseconds()
    }

    /**
     * For debug purposes only to generate dummy gym data
     */
    override suspend fun generateDummyData() = coroutineScope {
        val tz = TimeZone.currentSystemDefault()
        val today = Clock.System.now().toLocalDateTime(tz)
        val todayDate = today.date
        val threeMonthsAgo = todayDate.minus(3, DateTimeUnit.MONTH)
        val dateList = mutableListOf<Long>() // Change to store epochMillis
        var currentDate = threeMonthsAgo

        while (currentDate <= todayDate) {
            // Convert LocalDate to epochMillis and add to list
            dateList.add(currentDate.toEpochTimeStamp())
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
        }

        val dummyWoPlanName = listOf(
            "Latihan Biceps Barbell",
            "Latihan Triceps Kabel",
            "Latihan Kaki Leg Press",
            "Latihan Abs Crunches",
            "Latihan Chest Barbell",
            "Latihan Punggung Pull-Up",
            "Latihan Bahu Dumbbell",
            "Latihan Kardio Treadmill",
            "Latihan Kaki Squat",
            "Latihan Punggung Kabel",
        )
        val woPlanDescs = listOf(
            "Angkat barbel untuk biceps.",
            "Tarik kabel untuk triceps.",
            "Tekan beban dengan kaki.",
            "Kencangkan perut dengan crunches.",
            "Angkat barbel untuk dada.",
            "Tarik tubuh ke atas.",
            "Dumbbell untuk otot bahu.",
            "Lari di treadmill untuk kardio.",
            "Squat untuk kaki dan glutes.",
            "Latihan punggung dengan kabel.",
        )
        val weightInKg = listOf(6L, 12L, 14L, 16L, 18L, 6L, 12L, 14L, 16L, 18L)
        withContext(Dispatchers.IO) {
            // create workout plan
            repeat(dummyWoPlanName.size - 1) {
                workoutPlanDao.insertWorkoutPlan(
                    name = dummyWoPlanName[it],
                    description = woPlanDescs[it],
                    datetimeStamp = 0L,
                    orderingNumber = it,
                )
            }

            delay(2000)

            val listOfWoPlan = workoutPlanDao.getAllWorkoutPlan()
            val listOfAllExercise = exerciseDao.getAllExercises()
            if (listOfAllExercise.isEmpty()) return@withContext
            val listOfExerciseId = listOfAllExercise.map { it.id }
            listOfWoPlan.forEach { woPlan ->
                // create wo plan exercise
                repeat(5) {
                    // add 4 set for each exercise
                    val exerciseId = listOfExerciseId.random()
                    repeat(4) {
                        workoutPlanExerciseDao.insertWorkoutPlanExercise(
                            exerciseId = exerciseId,
                            workoutPlanId = woPlan.id,
                            reps = 12L,
                            weight = weightInKg.random(),
                            setNumberOrder = it + 1L,
                            finishedDateTime = 0L,
                        )
                    }
                }
            }

            delay(2000)

            // create exercise log
            listOfWoPlan.forEach { workoutPlan ->
                val listOfWorkoutPlanExercise = workoutPlanExerciseDao.getAllWorkoutPlanExercise(
                    workoutPlanId = workoutPlan.id,
                )
                // log exercise
                dateList.forEach { dateTimeStamp ->
                    listOfWorkoutPlanExercise.forEach { woExercise ->
                        exerciseLogDao.insertExerciseLog(
                            exerciseId = woExercise.exerciseId,
                            workoutPlanId = woExercise.workoutPlanId,
                            reps = listOf(6L, 12L).random(),
                            weight = weightInKg.random(),
                            difficulty = 0L,
                            measurement = "kg",
                            setNumberOrder = 0L,
                            finishedDateTime = dateTimeStamp,
                            logNotes = "Note $dateTimeStamp",
                            latitude = 0.0,
                            longitude = 0.0,

                        )
                    }
                }
            }
        }
    }
}
