export interface Student {
  id: string;
  studentName: string;
  marks: Marks[];
}

export interface Marks {
  id: string;
  subjectName: string;
  marks: number;
}

export interface IChartData {
  id: string;
  studentCount: number;
  creationDate: string
}

export interface SchoolAverage {
  id: string;
  schoolName: string;
  schoolAverage: number;
  subjectAverage: { [key: string]: number };
}
