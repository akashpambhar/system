import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ChartData, Student } from '../components/student/student.interface';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getAllStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(this.apiUrl);
  }

  getStudentById(id: string): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/student/id/${id}`);
  }

  getStudentByName(name: string): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/student/name/${name}`);
  }

  getChartData(): Observable<ChartData[]> {
    return this.http.get<ChartData[]>(`${this.apiUrl}/chart`);
  }
}
