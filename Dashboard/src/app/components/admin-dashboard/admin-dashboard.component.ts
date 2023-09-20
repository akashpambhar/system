import { Component, OnInit } from '@angular/core';
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";
import { StudentService } from "../../services/student.service";

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit {

  formData = {
    id: '',
    username: '',
    password: '',
    roles: ['ADMIN'],
    assignedSchool: [''],
    studentId : ''
  };

  assignedSchool = ['']

  selectedSchools: string[] = [];

  students: any;

  constructor(private authService: AuthService, private router: Router, private studentService: StudentService) { }

  ngOnInit(): void {
    this.getSchools()
  }

  signup() {
    this.authService.signup(this.formData)
      .subscribe(
        response => {
          this.router.navigate(['/']);
        },
        error => {
          console.error('Signup failed:', error);
        }
      );
  }

  getSchools() {
    this.studentService.getSchoolOfAdmin().subscribe((response: any) => {
      this.assignedSchool = response;
    })
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.authService.removeToken();
      this.router.navigate(['/']);
    })
  }

  onChangeSchool() {
    this.getAllStudentsFromSchool()
  }

  getAllStudentsFromSchool() {
    this.studentService.getAllStudentsFromSchool(this.formData.assignedSchool[0]).subscribe((students) => {
      this.students = students;
    })
  }
}
