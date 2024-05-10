package di

import data.repository.GymRepositoryImpl
import domain.repository.IGymRepository
import domain.usecase.GetWorkoutListUseCase
import features.home.HomeScreenViewModel
import features.inputExercise.InputExerciseScreenViewModel
import features.logWorkout.LogWorkoutScreenViewModel
import features.workoutHistory.WorkoutHistoryScreenViewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

fun letsKoinStart() {
    stopKoin()
    startKoin {
        modules(appModule())
    }
}

fun appModule() = module {

    single<IGymRepository> {
        GymRepositoryImpl()
    }

    single {
        GetWorkoutListUseCase(gymRepository = get())
    }

    single {
        HomeScreenViewModel(gymRepository = get(), getWorkoutListUseCase = get())
    }

    single {
        InputExerciseScreenViewModel(gymRepository = get())
    }

    single {
        LogWorkoutScreenViewModel(gymRepository = get())
    }

    single {
        WorkoutHistoryScreenViewModel(gymRepository = get())
    }

}