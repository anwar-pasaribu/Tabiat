package di

import data.repository.GymRepositoryImpl
import domain.repository.IGymRepository
import domain.usecase.GetExerciseByIdUseCase
import domain.usecase.GetExerciseListByWorkoutPlanUseCase
import domain.usecase.GetExerciseListUseCase
import domain.usecase.GetExerciseSetListUseCase
import domain.usecase.GetWorkoutPlanListUseCase
import domain.usecase.InputWorkoutPlanExerciseSetListUseCase
import domain.usecase.LogExerciseUseCase
import features.exerciseList.ExerciseListScreenViewModel
import features.home.HomeScreenViewModel
import features.inputExercise.InputExerciseScreenViewModel
import features.logWorkoutExercise.LogWorkoutExerciseScreenViewModel
import features.inputExercise.WorkoutDetailScreenViewModel
import features.inputWorkout.InputWorkoutScreenViewModel
import features.navigationHelper.NavigationViewModel
import features.workoutHistory.WorkoutHistoryScreenViewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

fun letsKoinStart() {
    stopKoin()
    startKoin {
        modules(appModule(), viewModels())
    }
}

fun appModule() = module {

    single<IGymRepository> {
        GymRepositoryImpl()
    }

    single {
        GetWorkoutPlanListUseCase(repository = get())
    }

    single {
        GetExerciseListByWorkoutPlanUseCase(repository = get())
    }

    single {
        InputWorkoutPlanExerciseSetListUseCase(repository = get())
    }

    single {
        GetExerciseListUseCase(repository = get())
    }

    single {
        GetExerciseSetListUseCase(repository = get())
    }

    single {
        GetExerciseByIdUseCase(repository = get())
    }

    single {
        LogExerciseUseCase(repository = get())
    }

}

fun viewModels() = module {
    single {
        NavigationViewModel()
    }
    single {
        HomeScreenViewModel(repository = get(), getWorkoutPlanListUseCase = get())
    }

    single {
        InputExerciseScreenViewModel(inputWorkoutPlanExerciseSetListUseCase = get())
    }

    single {
        InputWorkoutScreenViewModel(repository = get())
    }

    single {
        WorkoutHistoryScreenViewModel(repository = get())
    }

    single {
        WorkoutDetailScreenViewModel(repository = get(), getExerciseListByWorkoutPlanUseCase = get())
    }

    single {
        ExerciseListScreenViewModel(getExerciseListUseCase = get())
    }

    single {
        LogWorkoutExerciseScreenViewModel(
            getExerciseSetListUseCase = get(),
            getExerciseByIdUseCase = get(),
            logExerciseUseCase = get()
        )
    }
}