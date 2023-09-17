import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { StudentComponent } from './components/student/student.component';
import { MarksComponent } from './components/marks/marks.component';
import { TeacherGuard } from './teacher.guard';
import { AdminGuard } from './admin.guard';
import { ReportComponent } from './components/report/report.component';
import { SuperUserDashboardComponent } from './components/super-user-dashboard/super-user-dashboard.component';
import { SuperAdminGuard } from './super-admin.guard';
import { AdminDashboardComponent } from './components/admin-dashboard/admin-dashboard.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'super-admin-dashboard', component: SuperUserDashboardComponent, canActivate: [SuperAdminGuard] },
  { path: 'admin-dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'student', component: StudentComponent, canActivate: [AdminGuard] },
  { path: 'average', component: MarksComponent, canActivate: [TeacherGuard] },
  { path: 'report', component: ReportComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
