package cn.luoyulingfeng.dbindexresearch.data;

import cn.luoyulingfeng.dbindexresearch.mapper.TeacherMapper;
import cn.luoyulingfeng.dbindexresearch.model.Teacher;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TeacherDataGenerator {


    /**
     * 教师数据生成器
     * @param num
     */
    public static void generate(int num)throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("Teachers.csv"));
        Random random = new Random();
        String[] colleges = {"computer science college", "software engineering college", "iot college", "literature college", "music college", "fine arts college", "foreign language college"};

        long start = System.currentTimeMillis();
        int cnt = 0;
        for (int i=0; i< num; i++){
            cnt++;
            System.out.printf("%.2f%%\n", (cnt*1f / num) * 100);

            Teacher teacher = new Teacher();
            teacher.setId(i + 1);
            teacher.setTeaNo(String.valueOf(154987 * 1000 + i));
            teacher.setTeaName("teacher" + i);
            teacher.setTeaCollege(colleges[random.nextInt(colleges.length)]);
            writer.write(teacher.toString());
            writer.newLine();
        }
        writer.flush();
        writer.close();
        long end = System.currentTimeMillis();
        System.out.printf("time cost: %fs\n", (end - start)/1e3);
    }

    public static void main(String[] args) throws IOException{
        generate(1000);
    }
}
