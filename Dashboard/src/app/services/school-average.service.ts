import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SchoolAverage } from '../components/student/student.interface';

@Injectable({
  providedIn: 'root'
})
export class SchoolAverageService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getSchoolAverages(): Observable<SchoolAverage[]> {
    return this.http.get<SchoolAverage[]>(`${this.apiUrl}/average`);
  }
}
