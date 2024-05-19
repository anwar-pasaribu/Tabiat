package data.repository

import data.source.local.dao.IExerciseDao
import data.source.local.dao.IExerciseLogDao
import data.source.local.dao.IWorkoutPlanDao
import data.source.local.dao.IWorkoutPlanExerciseDao
import data.source.remote.api.IGymApi
import domain.model.gym.DifficultyLevel
import domain.model.gym.Exercise
import domain.model.gym.ExerciseLog
import domain.model.gym.ExerciseSet
import domain.model.gym.WorkoutPlan
import domain.model.gym.WorkoutPlanExercise
import domain.repository.IGymRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GymRepositoryImpl(
    private val gymApi: IGymApi,
    private val exerciseDao: IExerciseDao,
    private val workoutPlanDao: IWorkoutPlanDao,
    private val workoutPlanExerciseDao: IWorkoutPlanExerciseDao,
    private val exerciseLogDao: IExerciseLogDao
) : IGymRepository {

    override suspend fun createWorkoutPlan(workoutName: String, notes: String): Boolean {
        val latestWoPlan = workoutPlanDao.getLatestWorkoutPlan()
        workoutPlanDao.insertWorkoutPlan(
            name = workoutName,
            description = notes,
            datetimeStamp = Clock.System.now().toEpochMilliseconds(),
            orderingNumber = latestWoPlan.orderingNumber + 1
        )
        return true
    }

    override suspend fun getWorkoutPlanExercises(workoutPlanId: Long): List<Exercise> {
        val workoutPlanExercises = workoutPlanExerciseDao.getAllWorkoutPlanExercise(workoutPlanId)
        val exercises = mutableListOf<Exercise>()
        workoutPlanExercises.forEach { wpe ->
            val exercise = exerciseDao.getExerciseById(wpe.exerciseId)
            if (exercise != null) {
                exercises.add(exercise)
            }
        }
        return exercises.distinctBy { it.id }
    }

    override suspend fun getWorkoutPlanById(workoutPlanId: Long): WorkoutPlan {
        return workoutPlanDao.getWorkoutPlan(workoutPlanId)
    }

    override suspend fun logExercise(
        workoutPlanExerciseId: Long,
        workoutPlanId: Long,
        exerciseId: Long,
        reps: Int,
        weight: Int
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
            longitude = -1.0
        )

        workoutPlanExerciseDao.updateWorkoutPlanExerciseFinishedDateTime(
            workoutPlanExerciseId = workoutPlanExerciseId,
            finishedDateTime = finishedDateTime,
            reps = reps.toLong(),
            weight = weight.toLong()
        )

        return true
    }

    override suspend fun getExerciseLogListByDateTimeStamp(dateTimeStamp: Long): List<ExerciseLog> {
        val tz = TimeZone.currentSystemDefault()
        val endOfTimeStampOfSelectedDate = LocalTime(hour = 23, minute = 59, second = 59)
        val dateTime = LocalDateTime(
            date = Instant.fromEpochMilliseconds(dateTimeStamp).toLocalDateTime(tz).date,
            time = endOfTimeStampOfSelectedDate
        )
        val untilTimeStampMillis = dateTime.toInstant(tz).toEpochMilliseconds()

        val selectedDateTime = Instant.fromEpochMilliseconds(
            untilTimeStampMillis
        ).toLocalDateTime(tz)
        val firstSecondOfTheDay = LocalTime(hour = 0, minute = 0, second = 1)
        val dateTimeTodayMorning =
            LocalDateTime(date = selectedDateTime.date, time = firstSecondOfTheDay)
        val startTimeStampMillis = dateTimeTodayMorning.toInstant(tz).toEpochMilliseconds()
        return exerciseLogDao.getExerciseLogByDateTimeRange(
            startTimeStampMillis, untilTimeStampMillis
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
            targetMuscle = "",
            description = newExercise.description,
            type = 0L
        )
        return true
    }

    override suspend fun getExerciseSetList(
        workoutPlanId: Long,
        exerciseId: Long
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
        exerciseSetList: List<ExerciseSet>
    ): Boolean {
        exerciseSetList.forEach { exerciseSet ->
            workoutPlanExerciseDao.insertWorkoutPlanExercise(
                exerciseId = exerciseId,
                workoutPlanId = workoutPlanId,
                reps = exerciseSet.reps.toLong(),
                weight = exerciseSet.weight.toLong(),
                setNumberOrder = exerciseSet.setNumber.toLong(),
                finishedDateTime = 0L
            )
        }
        return true
    }

    override suspend fun getWorkoutPlans(): List<WorkoutPlan> {
        resetLastDayWorkoutPlanExerciseList()
        return workoutPlanDao.getAllWorkoutPlan()
    }

    private suspend fun resetLastDayWorkoutPlanExerciseList() = coroutineScope {
        withContext(Dispatchers.Default) {
            workoutPlanDao.getAllWorkoutPlan().forEach { workoutPlan ->
                workoutPlanExerciseDao.getAllWorkoutPlanExercise(workoutPlan.id).forEach { workoutPlanExercise ->
                    val isYesterday = isYesterday(workoutPlanExercise.finishedDateTime)
                    if (isYesterday) {
                        workoutPlanExerciseDao.updateWorkoutPlanExerciseFinishedDateTime(
                            workoutPlanExerciseId = workoutPlanExercise.id,
                            finishedDateTime = 0L,
                            reps = workoutPlanExercise.reps.toLong(),
                            weight = workoutPlanExercise.weight.toLong()
                        )
                    }
                }
            }
        }
    }

    private fun isYesterday(timestamp: Long): Boolean {
        val tz = TimeZone.currentSystemDefault()
        val yesterday = Clock.System.now().toLocalDateTime(tz).date.minus(1, DateTimeUnit.DAY)
        val timestampDate = Instant.fromEpochMilliseconds(timestamp).toLocalDateTime(tz).date

        return yesterday == timestampDate
    }

    override suspend fun getExercises(): List<Exercise> {

        val exercises = exerciseDao.getAllExercises()

        if (exercises.size <= 10) {
            loadRemoteExercises()
        }

        return exercises
    }

    private suspend fun loadRemoteExercises() = coroutineScope {
        val networkExercises = gymApi.loadExerciseList()
        withContext(Dispatchers.IO) {
            networkExercises.forEach { networkExercise ->
                val difficultyLevel = when (networkExercise.level) {
                    "beginner" -> DifficultyLevel.BEGINNER.level
                    "intermediate" -> DifficultyLevel.INTERMEDIATE.level
                    "expert" -> DifficultyLevel.EXPERT.level
                    else -> { 0L }
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
                        networkExercise.primaryMuscles.orEmpty() + networkExercise.secondaryMuscles.orEmpty()
                    ),
                    description = description,
                    type = 0L
                )
            }
        }
    }

    override suspend fun searchExercises(searchQuery: String): List<Exercise> {
        return exerciseDao.searchExercises(searchQuery)
    }
}