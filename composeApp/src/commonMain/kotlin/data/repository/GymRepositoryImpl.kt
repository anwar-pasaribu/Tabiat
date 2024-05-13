package data.repository

import data.source.local.dao.IExerciseDao
import data.source.local.dao.IExerciseLogDao
import data.source.local.dao.IWorkoutPlanDao
import data.source.local.dao.IWorkoutPlanExerciseDao
import domain.model.gym.Exercise
import domain.model.gym.ExerciseLog
import domain.model.gym.ExerciseSet
import domain.model.gym.WorkoutPlan
import domain.model.gym.WorkoutPlanExercise
import domain.repository.IGymRepository
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class GymRepositoryImpl(
    private val exerciseDao: IExerciseDao,
    private val workoutPlanDao: IWorkoutPlanDao,
    private val workoutPlanExerciseDao: IWorkoutPlanExerciseDao,
    private val exerciseLogDao: IExerciseLogDao
): IGymRepository {

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
            targetMuscle = newExercise.targetMuscle.toLong(),
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
        return workoutPlanDao.getAllWorkoutPlan()
    }

    override suspend fun getExercises(): List<Exercise> {
        return exerciseDao.getAllExercises()
    }
}