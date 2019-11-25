package cn.luoyulingfeng.dbindexresearch.data;

import cn.luoyulingfeng.dbindexresearch.model.Student;

import java.io.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class StudentDataGenerator {


    /**
     * 插入指定数量的学生记录
     * @param num   数量
     */
    public static void generate(int num)throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Students.csv"));
        Random random = new Random();
        String[] majors = new String[]{"computer science", "software engineering", "iot", "literature", "music", "fine arts", "foreign language"};
        Date[] admissionDates = new Date[]{Date.valueOf("2015-9-13"), Date.valueOf("2015-9-14"), Date.valueOf("2015-9-15")};

        long start = System.currentTimeMillis();
        int cnt = 0;
        for (int i=0; i<num; i++){
            cnt++;
            System.out.printf("%.2f%%\n", (cnt*1f / num) * 100);

            Student student = new Student();
            student.setId(i + 1);
            student.setStuNo(String.valueOf(192120 * 1000 + i));
            student.setStuName("student" + i);
            student.setStuGender(random.nextInt(2) + 1);
            student.setStuAge(random.nextInt(5) + 18);
            student.setStuMajor(majors[random.nextInt(majors.length)]);
            student.setStuType(random.nextInt(3));
            student.setStuGrade(random.nextInt(5));
            student.setStuClass(random.nextInt(10) + 1);
            student.setStuAdmissionDate(admissionDates[random.nextInt(admissionDates.length)]);
            writer.write(student.toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fs\n", (end - start)/1e3);
    }

    public static void main(String[] args)throws IOException {
        generate(1000);
    }

    public static List<Student> load(String file)throws IOException{
        List<Student> students = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = reader.readLine()) != null){
            String[] info = line.split(",");
            int id = Integer.parseInt(info[0]);
            String stuNo = info[1];
            String stuName = info[2];
            int stuGender = Integer.parseInt(info[3]);
            int stuAge = Integer.parseInt(info[4]);
            String stuMajor = info[5];
            int stuType = Integer.parseInt(info[6]);
            int stuGrade = Integer.parseInt(info[7]);
            int stuClass = Integer.parseInt(info[8]);
            Date date = Date.valueOf(info[9]);
            students.add(new Student(id, stuNo, stuName, stuGender, stuAge, stuMajor, stuType, stuGrade, stuClass, date));
        }
        reader.close();
        return students;
    }
}
