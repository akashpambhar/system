import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { IChartData, Student } from '../components/student/student.interface';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getAllStudents(): Observable<Student[]> {
    return this.http.get<Student[]>(this.apiUrl);
  }

  getStudentDashboard(studentId : string): Observable<Student[]>{
    return this.http.get<Student[]>(this.apiUrl + "/student/dashboard/" + studentId);
  }

  getAllStudentsFromSchool(schoolName:string): Observable<Student[]> {
    return this.http.get<Student[]>(this.apiUrl + "/student/school/" + schoolName);
  }

  getStudentById(id: string): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/student/id/${id}`);
  }

  getStudentByName(name: string): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/student/name/${name}`);
  }

  getChartData(): Observable<IChartData[]> {
    return this.http.get<IChartData[]>(`${this.apiUrl}/chart`);
  }

  uploadFile(file: any): Observable<any> {
    const formData = new FormData();

    formData.append('file', new Blob([file]), file.name);

    return this.http.post(`${this.apiUrl}/marks/csv`, formData);
  }

  getUserReport(): Observable<any>{
    return this.http.get<IChartData[]>(`${this.apiUrl}/school/upload-data`);
  }

  getSchools(): Observable<any>{
    return this.http.get(`${this.apiUrl}/school`);
  }

  getSchoolOfAdmin(): Observable<any>{
    return this.http.get(`${this.apiUrl}/school/admin`);
  }

  getClassWiseTopperBySchoolName(name: string){
    return this.http.get(`${this.apiUrl}/student/toppers/${name}`);
  }
}
