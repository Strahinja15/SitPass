// src/app/app.module.ts

import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './component/login/login.component';
import { RegisterComponent } from './component/register/register.component';
import { ProfileComponent } from './component/profile/profile.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthService } from './services/auth.service';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { CommonModule } from '@angular/common';
import { HomepageModule } from './component/homepage/homepage.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AccountRequestService } from './services/accountrequest.service';
import { UserService } from './services/user.service';
import { FacilityService } from './services/facility.service';
import { FacilityComponent } from './component/facility/facility.component';
import { DashboardComponent } from './component/dashboard/dashboard.component'; 
import { CommentService } from './services/comment.service';
import { DisciplineService } from './services/discipline.service';
import { ExerciseService } from './services/exercise.service';
import { ImageService } from './services/image.service';
import { RateService } from './services/rate.service';
import { ReviewService } from './services/review.service';
import { WorkdayService } from './services/workday.service';
@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    ProfileComponent,
    FacilityComponent,
    DashboardComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    HomepageModule,
    BrowserAnimationsModule
  ],
  providers: [
    AuthService,
    AccountRequestService,
    UserService,
    FacilityService,
    CommentService,
    DisciplineService,
    ExerciseService,
    ImageService,
    WorkdayService,
    RateService,
    ReviewService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
