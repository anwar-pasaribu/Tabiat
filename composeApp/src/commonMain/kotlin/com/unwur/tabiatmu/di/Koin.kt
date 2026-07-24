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
package com.unwur.tabiatmu.di

import com.unwur.tabiatmu.data.repository.GymRepositoryImpl
import com.unwur.tabiatmu.data.repository.PersonalizationRepository
import com.unwur.tabiatmu.data.source.local.createDatabase
import com.unwur.tabiatmu.data.source.local.dao.ExerciseDao
import com.unwur.tabiatmu.data.source.local.dao.ExerciseLogDao
import com.unwur.tabiatmu.data.source.local.dao.IExerciseDao
import com.unwur.tabiatmu.data.source.local.dao.IExerciseLogDao
import com.unwur.tabiatmu.data.source.local.dao.IWorkoutPersonalizationDao
import com.unwur.tabiatmu.data.source.local.dao.IWorkoutPlanDao
import com.unwur.tabiatmu.data.source.local.dao.IWorkoutPlanExerciseDao
import com.unwur.tabiatmu.data.source.local.dao.WorkoutPersonalizationDao
import com.unwur.tabiatmu.data.source.local.dao.WorkoutPlanDao
import com.unwur.tabiatmu.data.source.local.dao.WorkoutPlanExerciseDao
import com.unwur.tabiatmu.domain.repository.IGymRepository
import com.unwur.tabiatmu.domain.repository.IPersonalizationRepository
import com.unwur.tabiatmu.domain.usecase.CreateNewExerciseUseCase
import com.unwur.tabiatmu.domain.usecase.DeleteWorkoutPlanExerciseSetUseCase
import com.unwur.tabiatmu.domain.usecase.FilterExerciseByTargetMuscleCategoryUseCase
import com.unwur.tabiatmu.domain.usecase.GetExerciseByIdUseCase
import com.unwur.tabiatmu.domain.usecase.GetExerciseListByWorkoutPlanUseCase
import com.unwur.tabiatmu.domain.usecase.GetExerciseListUseCase
import com.unwur.tabiatmu.domain.usecase.GetExerciseLogCountByDateTimeStampUseCase
import com.unwur.tabiatmu.domain.usecase.GetExerciseLogListByDateTimeStampUseCase
import com.unwur.tabiatmu.domain.usecase.GetExerciseLogListByExerciseIdUseCase
import com.unwur.tabiatmu.domain.usecase.GetExerciseSetListUseCase
import com.unwur.tabiatmu.domain.usecase.GetGymPreferencesUseCase
import com.unwur.tabiatmu.domain.usecase.GetListExerciseCategoryUseCase
import com.unwur.tabiatmu.domain.usecase.GetRunningTimerPreferencesUseCase
import com.unwur.tabiatmu.domain.usecase.GetWorkoutPlanByIdUseCase
import com.unwur.tabiatmu.domain.usecase.GetWorkoutPlanListUseCase
import com.unwur.tabiatmu.domain.usecase.InputWorkoutPlanExerciseSetListUseCase
import com.unwur.tabiatmu.domain.usecase.LogExerciseUseCase
import com.unwur.tabiatmu.domain.usecase.ResetAllYesterdayActivitiesUseCase
import com.unwur.tabiatmu.domain.usecase.SaveRunningTimerPreferencesUseCase
import com.unwur.tabiatmu.domain.usecase.SearchExerciseUseCase
import com.unwur.tabiatmu.domain.usecase.UpdateWorkoutExerciseRepsAndWeightUseCase
import com.unwur.tabiatmu.domain.usecase.detail.GetExerciseListWithProgressByWorkoutPlanUseCase
import com.unwur.tabiatmu.domain.usecase.personalization.GetWorkoutPlanPersonalizationUseCase
import com.unwur.tabiatmu.domain.usecase.personalization.SetWorkoutPlanPersonalizationUseCase
import com.unwur.tabiatmu.features.createNewExercise.CreateExerciseScreenViewModel
import com.unwur.tabiatmu.features.exerciseDetail.ExerciseDetailScreenViewModel
import com.unwur.tabiatmu.features.exerciseList.ExerciseListScreenViewModel
import com.unwur.tabiatmu.features.home.HomeScreenViewModel
import com.unwur.tabiatmu.features.inputExercise.InputExerciseScreenViewModel
import com.unwur.tabiatmu.features.inputWorkout.InputWorkoutScreenViewModel
import com.unwur.tabiatmu.features.logWorkoutExercise.LogWorkoutExerciseScreenViewModel
import com.unwur.tabiatmu.features.navigationHelper.NavigationViewModel
import com.unwur.tabiatmu.features.settings.SettingScreenViewModel
import com.unwur.tabiatmu.features.workoutHistory.WorkoutHistoryScreenViewModel
import com.unwur.tabiatmu.features.workoutPlanDetail.WorkoutDetailScreenViewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes
import org.koin.dsl.module

fun letsKoinStart(configuration: KoinAppDeclaration? = null) {
    startKoin {
        includes(configuration)
        modules(
            dataStoreModule(),
            databaseModule(),
            getNetworkModule(),
            appModule(),
            viewModels()
        )
        printLogger(Level.DEBUG)
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

    singleOf(::GetExerciseLogCountByDateTimeStampUseCase)

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

    singleOf(::GetExerciseListWithProgressByWorkoutPlanUseCase)
}

fun viewModels() = module {
    singleOf(::NavigationViewModel)

    singleOf(::HomeScreenViewModel)

    factoryOf(::InputExerciseScreenViewModel)

    factoryOf(::InputWorkoutScreenViewModel)

    singleOf(::WorkoutHistoryScreenViewModel)

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
