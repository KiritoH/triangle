import com.yww.common.Const;
import com.yww.entity.OutResult;
import com.yww.entity.Triangle;
import com.yww.util.ExcelUtil;
import com.yww.util.JudgeUtil;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 进行单元测试：
 */
public class JudgeTest {


    /**
     * 白盒判定覆盖测试，输入输出具体文件在resources/white_judge下
     * @throws Exception
     */
    @Test
    public void triangleTest() throws Exception {
        //由于将测试代码封装在函数内，所以仅需调用即可

        //判定覆盖测试用例
        testFun(0);
        //路径覆盖测试用例
        testFun(1);
    }




    /**
     * 为了方便观察，将单元测试方法封装了一下，分别对应resource下的两个文件夹的输入输出excel文件
     * type = 0时 即judge（判定覆盖测试用例）
     * type = 1时 即path（路径覆盖测试用例）
     */
    public static void testFun(int type) throws Exception {
        String in = "";
        String out = "";
        if (type == 0){
            in = System.getProperty("user.dir")+"/src/main/resources/white_judge/in.xls";
            out = System.getProperty("user.dir")+"/src/main/resources/white_judge/out.xls";
        }else {
            in = System.getProperty("user.dir")+"/src/main/resources/white_path/in.xls";
            out = System.getProperty("user.dir")+"/src/main/resources/white_path/out.xls";
        }


        JudgeUtil judgeUtil = new JudgeUtil();
        //如何自动测试呢？
        /**
         * 首先要得到测试用例的id,输入数据,预期输出
         * 然后通过我们的写的judge判断，将预期输出与实际输出进行对比
         * 然后得到测试结果，并输出通过率
         */
        //先读取测试用例的excel
        List<Map<String, Object>> impList;
        String keys[] = {"id","side_A","side_B","side_C","expectOutput"};
        //测试用例有五项，id,side_A,side_B,side_C,expectOutput
        impList = ExcelUtil.imp(in,keys);
        //对测试用例进行判断
        //将每组的边进行预测，得到实际输出，然后将实际输出与预期输出进行对比
        //输出一共三列，id，实际输出，结果
        List<OutResult> outList = new ArrayList<OutResult>();
        OutResult outResult;
        for (Map<String,Object> map:impList ){
            outResult = new OutResult();
            Double Id = (Double) map.get("id");
            int id = Id.intValue();
            outResult.setId(id);
            //得到实际输出
            Double a = (Double) map.get("side_A");
            Double b = (Double) map.get("side_B");
            Double c = (Double) map.get("side_C");

            int actual = judgeUtil.judge(a,b,c);
            Double expectDou = (Double) map.get("expectOutput");
            int expect = expectDou.intValue();
            outResult.setActualOutput(actual);
            //将实际输出和预期输出进行对比
            if (expect == actual){
                outResult.setResult(Const.Result.PASS);
            }else {
                outResult.setResult(Const.Result.FAIL);

            }
            outList.add(outResult);
        }
        String [] str1 = {"Id","ActualOutput","Result"};
        String [] str2 = {"id","actualOutput","result"};
        //输出结果
        ExcelUtil.export(out,"1",outList,str1,str2);

    }


}
