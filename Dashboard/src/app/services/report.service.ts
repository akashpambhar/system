import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  private studentURL = 'http://localhost:1010/student-performance';
  private schoolURL = 'http://localhost:1010/school-performance';

  constructor(private http: HttpClient) { }

  getStudentPerformanceByClassName(className: string) {
    this.http.get(this.studentURL + "/class/" + className).subscribe(
      (response) => {
        
      },
      (error) => {
        alert('Some error occured');
      }
    );
  }

  getAllStudentPerformance() {
    this.http.get(this.studentURL + "/all").subscribe(
      (response) => {
        
      },
      (error) => {
        alert('Some error occured');
      }
    );
  }

  getStudentPerformanceByStudentName(studentName: string) {
    this.http.get(this.studentURL + "/student/" + studentName).subscribe(
      (response) => {
        
      },
      (error) => {
        alert('Some error occured');
      }
    );
  }

  getSchoolPerformanceByClassName(className: string) {
    this.http.get(this.schoolURL + "/" + className).subscribe(
      (response) => {
        
      },
      (error) => {
        alert('Some error occured');
      }
    );
  }

  getSchoolPerformanceByClassNameSubjectName(className: string, subjectName:string) {
    this.http.get(this.schoolURL + "/" + className + "/" + subjectName).subscribe(
      (response) => {
        
      },
      (error) => {
        alert('Some error occured');
      }
    );
  }
}
