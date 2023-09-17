import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private apiUserUrl = 'http://localhost:8080/api/user'

  constructor(private http: HttpClient) { }

  getAllRoles(){
    return this.http.get(`${this.apiUserUrl}/roles`)
  }

  getAllAdminUsers(){
    return this.http.get(`${this.apiUserUrl}`)
  }

  editUser(formData : any, id : string): Observable<any>{
    return this.http.put(`${this.apiUserUrl}/${id}`, formData);
  }
}
