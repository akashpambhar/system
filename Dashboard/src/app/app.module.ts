import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NgChartsModule } from 'ng2-charts';
import { HomeComponent } from './components/home/home.component';
import { StudentComponent } from './components/student/student.component';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { WebSocketService } from './services/web-socket.service';
import { FormsModule } from '@angular/forms';
import { MarksComponent } from './components/marks/marks.component';
import { AuthInterceptor } from './auth.interceptor';
import { StudentPerformanceClassnameComponent } from './components/student-performance-classname/student-performance-classname.component';
import { ReportComponent } from './components/report/report.component';
import { StudentPerformanceAllComponent } from './components/student-performance-all/student-performance-all.component';
import { StudentPerformanceStudentnameComponent } from './components/student-performance-studentname/student-performance-studentname.component';
import { SchoolPerformanceClassnameSubjectnameComponent } from './components/school-performance-classname-subjectname/school-performance-classname-subjectname.component';
import { SchoolPerformanceClassnameComponent } from './components/school-performance-classname/school-performance-classname.component';
import { SuperUserDashboardComponent } from './components/super-user-dashboard/super-user-dashboard.component';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    StudentComponent,
    MarksComponent,
    StudentPerformanceClassnameComponent,
    ReportComponent,
    StudentPerformanceAllComponent,
    StudentPerformanceStudentnameComponent,
    SchoolPerformanceClassnameSubjectnameComponent,
    SchoolPerformanceClassnameComponent,
    SuperUserDashboardComponent,
    AdminDashboardComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    NgChartsModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [
    WebSocketService,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
