import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  formData = {
    username: '',
    password: '',
    roles: ['ADMIN']
  };

  constructor(private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
  }

  signup() {
    this.authService.signup(this.formData)
      .subscribe(
        response => {
          console.log('Signup successful:', response);
          this.router.navigate(['/']);
        },
        error => {
          console.error('Signup failed:', error);
        }
      );
  }
}
