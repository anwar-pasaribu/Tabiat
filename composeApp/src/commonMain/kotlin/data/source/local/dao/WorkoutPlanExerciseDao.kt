package data.source.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.unwur.tabiatmu.database.TabiatDatabase
import domain.model.gym.WorkoutPlanExercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutPlanExerciseDao(
    private val database: TabiatDatabase
) : IWorkoutPlanExerciseDao {
    override suspend fun getAllWorkoutPlanExerciseObservable(workoutPlanId: Long): Flow<List<WorkoutPlanExercise>> {
        return database
            .workoutPlanExerciseQueries
            .selectAllWorkoutPlanExercise(workoutPlanId)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { woPlanExerciseEntityList ->
                woPlanExerciseEntityList.map {
                    WorkoutPlanExercise(
                        it.id,
                        it.exerciseId,
                        it.workoutPlanId,
                        it.reps.toInt(),
                        it.weight.toInt(),
                        it.setNumberOrder.toInt(),
                        it.finishedDateTime
                    )
                }
            }
    }

    override suspend fun getAllWorkoutPlanExercise(workoutPlanId: Long): List<WorkoutPlanExercise> {
        return database
            .workoutPlanExerciseQueries
            .selectAllWorkoutPlanExercise(workoutPlanId)
            .executeAsList()
            .map {
                WorkoutPlanExercise(
                    it.id,
                    it.exerciseId,
                    it.workoutPlanId,
                    it.reps.toInt(),
                    it.weight.toInt(),
                    it.setNumberOrder.toInt(),
                    it.finishedDateTime
                )
            }
    }

    override suspend fun getLatestWorkoutPlanExercise(): WorkoutPlanExercise {
        return database
            .workoutPlanExerciseQueries
            .selectLatestWorkoutPlanExercise { id, exerciseId, workoutPlanId, reps, weight, setNumberOrder, finishedDateTime ->
                WorkoutPlanExercise(
                    id,
                    exerciseId,
                    workoutPlanId,
                    reps.toInt(),
                    weight.toInt(),
                    setNumberOrder.toInt(),
                    finishedDateTime
                )
            }.executeAsOne()
    }

    override suspend fun insertWorkoutPlanExercise(
        exerciseId: Long,
        workoutPlanId: Long,
        reps: Long,
        weight: Long,
        setNumberOrder: Long,
        finishedDateTime: Long
    ) {
        database
            .workoutPlanExerciseQueries.insertWorkoutPlanExercise(
                exerciseId,
                workoutPlanId,
                reps,
                weight,
                setNumberOrder,
                finishedDateTime
            )
    }

    override suspend fun updateWorkoutPlanExerciseFinishedDateTime(
        workoutPlanExerciseId: Long,
        finishedDateTime: Long,
        reps: Long,
        weight: Long
    ) {
        database
            .workoutPlanExerciseQueries
            .updateWorkoutPlanExerciseFinishedDateTime(
                finishedDateTime = finishedDateTime,
                reps = reps,
                weight = weight,
                id = workoutPlanExerciseId,
            )
    }
}