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
    assignedSchool : ['']
  };

  roles = [];
  assignedSchool = ['']

  users: any;

  constructor(private userService: UserService, private router: Router, private authService: AuthService, private studentService: StudentService) { }

  ngOnInit(): void {
    this.loadAllAdminUser()
    this.getSchools()
  }

  signup() {
    if (this.formData.id != "") {
      console.log(this.formData.id);
      this.editUser();
    }
    else {
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
  }

  loadAllAdminUser() {
    this.userService.getAllAdminUsers().subscribe(
      (response: any) => {
        this.users = response
      }
    )
  }

  SetUserToForm(user: any) {
    this.formData.id = user.id;
    this.formData.username = user.username
    this.formData.password = user.password
    this.formData.roles = user.roles

  }

  editUser() {
    this.userService.editUser(this.formData, this.formData.id).subscribe(response => {
      console.log('Edit API response:', response);
      this.loadAllAdminUser()
    })
  };

  getSchools(){
    this.studentService.getSchools().subscribe((response : any) => {
      this.assignedSchool = response;
    })
  }

  clearForm() {
    this.formData.id = '';
    this.formData.username = '';
    this.formData.password = '';
    this.formData.roles = ['ADMIN'];
  }
}