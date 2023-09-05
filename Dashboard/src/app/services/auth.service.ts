import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import jwt_decode from "jwt-decode";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';
  private tokenSubject = new BehaviorSubject<string>("");

  constructor(private http: HttpClient, private cookieService: CookieService) {
    const cookieValue = this.cookieService.get('system');
    if (cookieValue) {
      // Set the initial token value if it exists in the cookie
      this.tokenSubject.next(cookieValue);
    }
  }

  login(username: string, password: string): Observable<any> {
    const body = { username, password };
    return this.http.post(`${this.apiUrl}/signin`, body, {
      observe: 'response'
    })
      .pipe(
        tap(response => {
          const body: any = response?.body;
          if (body.token) {
            this.tokenSubject.next(body.token);
            this.cookieService.set('system', body.token);
          }
        })
      );
  }

  signup(formData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/signup`, formData);
  }

  getToken(): string {
    return this.tokenSubject.value;
  }

  getRole(): string {
    const token = this.getToken();
    if (token) {
      const decodedToken: any = jwt_decode(token);

      return decodedToken?.roles[0].authority;
    }
    return "";
  }
}
