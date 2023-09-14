import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.scss']
})
export class ReportComponent implements OnInit {

  selectedElement: string = 'select-report-type';

  constructor() { }

  ngOnInit(): void {
  }

}
