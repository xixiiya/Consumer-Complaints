import java.io.*;
import java.util.*;

public class complaints {

    public static void main(String[] args) throws Exception {
        // input and output file path
        String fildPath = "../input/complaints.csv";
        String outputPath = "../output/results.csv";
        // collect input information
        System.out.println("get input data...");
        List datas = getData(fildPath);
        System.out.println("get data success!");
        System.out.println("record size : " + datas.size());

        // process information
        System.out.println("data cleaning...");
        Map resultMap = dataCleaning(datas);

        // calculate column
        System.out.println("calculating...");
        List dataList = dataCalculation(resultMap);

        //  sort by product name
        sortList(dataList);

        // formatting
        boolean result = outputFile(outputPath,dataList);

        if(result) {
            System.out.println("export data success!");
        } else {
            System.out.println("export data error!");
        }
    }

    /**
     * Sort by product name asceding then by date asceding
     *
     * @param datas
     */
    public static void sortList(List datas) {
        Collections.sort(datas, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                String[] o1s = o1.toString().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",-1);
                String[] o2s = o2.toString().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",-1);

                String product1 = o1s[0].replace("\"","");
                String product2 = o2s[0].replace("\"","");

                String year1 = o1s[1];
                String year2 = o2s[1];

                if(product1.compareTo(product2) == 0) {
                    year1.compareTo(year2);
                }else {
                    return product1.compareTo(product2);
                }

                return 0;
            }
        });

    }

    /**
     * output
     * @param outputPath output file path
     * @param dataList
     * @return
     */
    public static boolean outputFile(String outputPath,List dataList) {
        File file = new File(outputPath);
        boolean isSucess=false;

        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw =new BufferedWriter(osw);
            if(dataList!=null && !dataList.isEmpty()){
                for(Object data : dataList){
                    bw.append(data.toString()).append("\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSucess;
    }
    /**
     * calculate number
     * @param dataMap
     * @return
     */
    public static List dataCalculation(Map dataMap) {
        List datas = new ArrayList();

        Set set = dataMap.keySet();
        Iterator it = set.iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            Map tempMap = (Map) dataMap.get(key);

            String year = key.split("-")[0];
            String product = key.split("-")[1];
            String complaints_count = tempMap.get("complaints_count").toString();
            String company_count = tempMap.get("company_count").toString();

            Map companyMap = (Map) tempMap.get("companies");
            Set companySet = companyMap.keySet();
            Iterator cpIt = companySet.iterator();
            int maxCount = 0;
            while (cpIt.hasNext()) {
                String cpKey = cpIt.next().toString();
                int thisCount = Integer.parseInt(companyMap.get(cpKey).toString());
                if(thisCount > maxCount) {
                    maxCount = thisCount;
                }
            }

            int maxCountPercent = getPercent(maxCount,Integer.parseInt(complaints_count));

            StringBuffer row = new StringBuffer();
            row.append(product).append(",")
                    .append(year).append(",")
                    .append(complaints_count).append(",")
                    .append(company_count).append(",")
                    .append(maxCountPercent);

            datas.add(row.toString());
        }
        return datas;
    }

    /**
     * a / b ,
     * @param a
     * @param b
     * @return
     */
    public static int getPercent(int a,int b) {
        float float_a = Float.parseFloat(a + "");
        float temp = float_a / b ;
        return (int)(Math.round(temp * 100));
    }
    /**
     *
     * @param datas
     * @return
     */
    public static Map dataCleaning(List datas) {
        Map cleanData = new HashMap();
        for(int i = 0; i < datas.size(); i++) {
            Map tempMap = new HashMap();
            List data = (List) datas.get(i);
            String year = data.get(0).toString();
            String product = data.get(1).toString();
            String company = data.get(2).toString();
            String key = year  + "-"+ product;
            if(cleanData.containsKey(key)) {
                tempMap = (Map) cleanData.get(key);

                // calculate number of complaints
                int complaints_count = Integer.parseInt(tempMap.get("complaints_count").toString());
                complaints_count ++ ;
                tempMap.put("complaints_count",complaints_count);

                // calculate number of company
                Map companyMap = (Map) tempMap.get("companies");
                if(companyMap.containsKey(company)) {
                    int company_complaints_count = Integer.parseInt(companyMap.get(company).toString());
                    company_complaints_count++;
                    companyMap.put(company,company_complaints_count);
                } else {
                    int company_count = Integer.parseInt(tempMap.get("company_count").toString()) + 1;
                    companyMap.put(company,1);
                    tempMap.put("company_count",company_count);
                    tempMap.put("companies",companyMap);
                }

            } else {

                //
                tempMap.put("complaints_count",1);
                //
                tempMap.put("company_count",1);

                //
                Map companyMap = new HashMap();
                companyMap.put(company,1);

                tempMap.put("companies",companyMap);
            }

            cleanData.put(key,tempMap);
        }

        return cleanData;
    }

    /**

     *
     * @param path
     * @return
     * @throws IOException
     */
    public static List getData(String path) throws IOException {
        List allData = new ArrayList();
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(path);
        }catch (Exception e) {
            System.out.println("path error!");
            throw new IOException();
        }
        InputStreamReader isw = new InputStreamReader(fis, "utf-8");
        BufferedReader br = new BufferedReader(isw);
        String line = "";
        int i = 0;
        boolean flag = true;

        while (flag){
            i++;
            List tempList = new ArrayList();
            StringBuffer readLine = new StringBuffer();
            boolean bReadNext = true;
            while (bReadNext) {
                String strReadLine = "";
                if((strReadLine = br.readLine()) == null){
                    flag = false;
                    break;
                }
                if (readLine.length() > 0) {
                    readLine.append("\r\n");
                }

                if (strReadLine == null) {
                    break;
                }
                readLine.append(strReadLine);


                if (countChar(readLine.toString(), '"', 0) % 2 == 1) {
                    bReadNext = true;
                } else {
                    bReadNext = false;
                }
            }
            if(i == 1){
                continue;
            }
            if(readLine != null && readLine.toString() != ""){
                //
                String[] strArr = readLine.toString().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)",-1);
                try{
                    String date = strArr[0];
                    // seperate year
                    String year = date.split("-")[0];
                    tempList.add(year);
                    // seperate product
                    tempList.add(strArr[1]);
                    // seperate company
                    tempList.add(strArr[7]);
                    allData.add(tempList);
                }catch (Exception e){
                    //
                }

            }

        }

        System.out.println(i);
        return allData;
    }

    /**
     *calculate number
     *
     * @param str character list
     * @param c character
     * @param start  start position
     * @return number
     */
    private static int countChar(String str, char c, int start) {
        int i = 0;
        int index = str.indexOf(c, start);
        return index == -1 ? i : countChar(str, c, index + 1) + 1;
    }

}
