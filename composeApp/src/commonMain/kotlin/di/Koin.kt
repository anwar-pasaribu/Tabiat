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
package di

import data.repository.GymRepositoryImpl
import data.repository.PersonalizationRepository
import data.source.local.createDatabase
import data.source.local.dao.ExerciseDao
import data.source.local.dao.ExerciseLogDao
import data.source.local.dao.IExerciseDao
import data.source.local.dao.IExerciseLogDao
import data.source.local.dao.IWorkoutPersonalizationDao
import data.source.local.dao.IWorkoutPlanDao
import data.source.local.dao.IWorkoutPlanExerciseDao
import data.source.local.dao.WorkoutPersonalizationDao
import data.source.local.dao.WorkoutPlanDao
import data.source.local.dao.WorkoutPlanExerciseDao
import domain.repository.IGymRepository
import domain.repository.IPersonalizationRepository
import domain.usecase.CreateNewExerciseUseCase
import domain.usecase.DeleteWorkoutPlanExerciseSetUseCase
import domain.usecase.FilterExerciseByTargetMuscleCategoryUseCase
import domain.usecase.GetExerciseByIdUseCase
import domain.usecase.GetExerciseListByWorkoutPlanUseCase
import domain.usecase.GetExerciseListUseCase
import domain.usecase.GetExerciseLogListByDateTimeStampUseCase
import domain.usecase.GetExerciseLogListByExerciseIdUseCase
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
import domain.usecase.UpdateWorkoutExerciseRepsAndWeightUseCase
import domain.usecase.personalization.GetWorkoutPlanPersonalizationUseCase
import domain.usecase.personalization.SetWorkoutPlanPersonalizationUseCase
import features.createNewExercise.CreateExerciseScreenViewModel
import features.exerciseDetail.ExerciseDetailScreenViewModel
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
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun letsKoinStart(initialModule: List<Module> = emptyList()) {
    stopKoin()
    startKoin {
        if (initialModule.isNotEmpty()) {
            modules(
                initialModule.first(),
                dataStoreModule(),
                databaseModule(),
                getNetworkModule(),
                appModule(),
                viewModels()
            )
        } else {
            modules(
                dataStoreModule(),
                databaseModule(),
                getNetworkModule(),
                appModule(),
                viewModels()
            )
        }
    }
}

fun appModule() = module {
    single<IGymRepository> {
        GymRepositoryImpl(
            personalizationRepository = get(),
            gymApi = get(),
            exerciseDao = get(),
            workoutPlanDao = get(),
            workoutPlanExerciseDao = get(),
            exerciseLogDao = get(),
            preferencesDataSource = get(),
        )
    }

    single<IPersonalizationRepository> {
        PersonalizationRepository(
            workoutPersonalizationDao = get(),
            preferencesDataSource = get(),
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
    singleOf(::GetExerciseLogListByExerciseIdUseCase)

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
    singleOf(::UpdateWorkoutExerciseRepsAndWeightUseCase)

    singleOf(::SetWorkoutPlanPersonalizationUseCase)
    singleOf(::GetWorkoutPlanPersonalizationUseCase)
}

fun viewModels() = module {
    singleOf(::NavigationViewModel)

    singleOf(::HomeScreenViewModel)

    factoryOf(::InputExerciseScreenViewModel)

    factoryOf(::InputWorkoutScreenViewModel)

    factoryOf(::WorkoutHistoryScreenViewModel)

    singleOf(::WorkoutDetailScreenViewModel)

    singleOf(::ExerciseListScreenViewModel)

    factoryOf(::LogWorkoutExerciseScreenViewModel)

    factoryOf(::ExerciseDetailScreenViewModel)

    singleOf(::CreateExerciseScreenViewModel)

    singleOf(::SettingScreenViewModel)
}

fun databaseModule() = module {
    single {
        createDatabase()
    }

    single<IExerciseDao> {
        ExerciseDao(database = get())
    }

    single<IWorkoutPlanDao> {
        WorkoutPlanDao(database = get())
    }

    single<IWorkoutPlanExerciseDao> {
        WorkoutPlanExerciseDao(database = get())
    }

    single<IExerciseLogDao> {
        ExerciseLogDao(database = get())
    }

    single<IWorkoutPersonalizationDao> {
        WorkoutPersonalizationDao(database = get())
    }
}
