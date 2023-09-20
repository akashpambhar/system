import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';
import { StudentService } from 'src/app/services/student.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-super-user-dashboard',
  templateUrl: './super-user-dashboard.component.html',
  styleUrls: ['./super-user-dashboard.component.scss']
})
export class SuperUserDashboardComponent implements OnInit {
  formData = {
    id: '',
    username: '',
    password: '',
    roles: ['ADMIN'],
    assignedSchool : null
  };

  roles = [];

  users: any;

  constructor(private userService: UserService, private router: Router, private authService: AuthService, private studentService: StudentService) { }

  ngOnInit(): void {
    this.loadAllAdminUser()
  }

  signup() {

    const schools : any = this.formData.assignedSchool;
    this.formData.assignedSchool = schools.split(",")

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

  loadAllAdminUser() {
    this.userService.getAllAdminUsers().subscribe(
      (response: any) => {
        this.users = response
      }
    )
  }

  clearForm() {
    this.formData.id = '';
    this.formData.username = '';
    this.formData.password = '';
    this.formData.roles = ['ADMIN'];
  }

  logout() {
    this.authService.logout().subscribe(() => {
      this.authService.removeToken();
      this.router.navigate(['/']);
    })
  }
}