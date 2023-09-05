import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  formData = {
    username: '',
    password: ''
  };


  constructor(private authService: AuthService,
    private router: Router) { }

  ngOnInit(): void {

  }

  login() {
    this.authService.login(this.formData.username, this.formData.password)
      .subscribe(
        response => {
          if (this.authService.getRole() === "ROLE_ADMIN") {
            this.router.navigate(['/student']);
          } else if (this.authService.getRole() === "ROLE_TEACHER") {
            this.router.navigate(['/average']);
          }
        },
        error => {
          console.error('Login failed:', error);
        }
      );
  }

}
