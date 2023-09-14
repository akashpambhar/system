import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { StudentComponent } from './components/student/student.component';
import { SignupComponent } from './components/signup/signup.component';
import { MarksComponent } from './components/marks/marks.component';
import { TeacherGuard } from './teacher.guard';
import { AdminGuard } from './admin.guard';
import { ReportComponent } from './components/report/report.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'student', component: StudentComponent, canActivate: [AdminGuard] },
  { path: 'average', component: MarksComponent, canActivate: [TeacherGuard] },
  { path: 'report', component: ReportComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }