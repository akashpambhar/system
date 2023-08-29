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

export interface ChartData {
  id: string;
  studentCount: number;
}
