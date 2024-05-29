package di

import data.repository.GymRepositoryImpl
import data.source.local.createDatabase
import data.source.local.dao.ExerciseDao
import data.source.local.dao.ExerciseLogDao
import data.source.local.dao.IExerciseDao
import data.source.local.dao.IExerciseLogDao
import data.source.local.dao.IWorkoutPlanDao
import data.source.local.dao.IWorkoutPlanExerciseDao
import data.source.local.dao.WorkoutPlanDao
import data.source.local.dao.WorkoutPlanExerciseDao
import domain.repository.IGymRepository
import domain.usecase.CreateNewExerciseUseCase
import domain.usecase.DeleteWorkoutPlanExerciseSetUseCase
import domain.usecase.FilterExerciseByTargetMuscleCategoryUseCase
import domain.usecase.GetExerciseByIdUseCase
import domain.usecase.GetExerciseListByWorkoutPlanUseCase
import domain.usecase.GetExerciseListUseCase
import domain.usecase.GetExerciseLogListByDateTimeStampUseCase
import domain.usecase.GetExerciseSetListUseCase
import domain.usecase.GetGymPreferencesUseCase
import domain.usecase.GetListExerciseCategoryUseCase
import domain.usecase.GetRunningTimerPreferencesUseCase
import domain.usecase.GetWorkoutPlanByIdUseCase
import domain.usecase.GetWorkoutPlanListUseCase
import domain.usecase.InputWorkoutPlanExerciseSetListUseCase
import domain.usecase.LogExerciseUseCase
import domain.usecase.ResetAllYesterdayActivitiesUseCase
import domain.usecase.SaveRunningTimerPreferencesUseCase
import domain.usecase.SearchExerciseUseCase
import features.createNewExercise.CreateExerciseScreenViewModel
import features.exerciseList.ExerciseListScreenViewModel
import features.home.HomeScreenViewModel
import features.inputExercise.InputExerciseScreenViewModel
import features.inputWorkout.InputWorkoutScreenViewModel
import features.logWorkoutExercise.LogWorkoutExerciseScreenViewModel
import features.navigationHelper.NavigationViewModel
import features.settings.SettingScreenViewModel
import features.workoutHistory.WorkoutHistoryScreenViewModel
import features.workoutPlanDetail.WorkoutDetailScreenViewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun letsKoinStart() {
    stopKoin()
    startKoin {
        modules(dataStoreModule(), databaseModule(), getNetworkModule(), appModule(), viewModels())
    }
}

fun appModule() = module {

    single<IGymRepository> {
        GymRepositoryImpl(
            gymApi = get(),
            exerciseDao = get(),
            workoutPlanDao = get(),
            workoutPlanExerciseDao = get(),
            exerciseLogDao = get(),
            preferencesDataSource = get()
        )
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

    single {
        CreateNewExerciseUseCase(repository = get())
    }

    single {
        GetWorkoutPlanByIdUseCase(repository = get())
    }

    single {
        GetExerciseLogListByDateTimeStampUseCase(repository = get())
    }

    single {
        SearchExerciseUseCase(repository = get())
    }

    single {
        DeleteWorkoutPlanExerciseSetUseCase(get())
    }

    single {
        GetGymPreferencesUseCase(get())
    }

    single {
        ResetAllYesterdayActivitiesUseCase(get())
    }

    singleOf(::GetListExerciseCategoryUseCase)
    singleOf(::FilterExerciseByTargetMuscleCategoryUseCase)
    singleOf(::GetRunningTimerPreferencesUseCase)
    singleOf(::SaveRunningTimerPreferencesUseCase)
}

fun viewModels() = module {

    singleOf(::NavigationViewModel)

    single {
        HomeScreenViewModel(
            repository = get(),
            resetAllYesterdayActivitiesUseCase = get()
        )
    }

    single {
        InputExerciseScreenViewModel(inputWorkoutPlanExerciseSetListUseCase = get())
    }

    single {
        InputWorkoutScreenViewModel(repository = get())
    }

    factoryOf(::WorkoutHistoryScreenViewModel)

    factoryOf(::WorkoutDetailScreenViewModel)

    singleOf(::ExerciseListScreenViewModel)

    factoryOf(::LogWorkoutExerciseScreenViewModel)

    singleOf(::CreateExerciseScreenViewModel)

    singleOf(::SettingScreenViewModel)
}

fun databaseModule() = module {
    single {
        createDatabase()
    }

    single<IExerciseDao>{
        ExerciseDao(database = get())
    }

    single<IWorkoutPlanDao>{
        WorkoutPlanDao(database = get())
    }

    single<IWorkoutPlanExerciseDao>{
        WorkoutPlanExerciseDao(database = get())
    }

    single<IExerciseLogDao>{
        ExerciseLogDao(database = get())
    }
}