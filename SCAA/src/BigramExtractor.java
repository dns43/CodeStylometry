import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


public class BigramExtractor {
	  

    public static void main(String[] args) throws IOException
	{   	
      	Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
       	int month = cal.get(Calendar.MONTH);
       	int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
       	String time = sdf.format(cal.getTime());
    	String output_filename = "/Users/Aylin/Desktop/Drexel/2014/ARLInternship/SCAAarffs/bigramArffs/"
    			+(month+1) + "." + dayOfMonth + "_" +
    			"9FilesExactlyPerAuthor_2012_validation_exact_bigrams.arff" ;
	
    	String dirPath="/Users/Aylin/Desktop/Drexel/2014/ARLInternship/SCAA_Datasets/"
    			+"bigExperiments/250authors/9FilesExactlyPerAuthor_2012_validation_exact_allfeatures/";      	List test_file_paths = Util.listTextFiles(dirPath);
      	

	String text = "";
  	//Writing the test arff
  	//first specify relation
	Util.writeFile("@relation 9FilesExactlyPerAuthor_2012_validation_bigrams"+"\n"+"\n", output_filename, true);
	Util.writeFile("@attribute instanceID {", output_filename, true);
   	List test_cpp_paths = Util.listCPPFiles(dirPath);
   	for(int j=0; j < test_cpp_paths.size();j++ )
	{
		File fileCPP = new File(test_cpp_paths.get(j).toString());
		String fileName = fileCPP.getName();
		Util.writeFile(fileName+",", output_filename, true);
		if ((j+1)==test_cpp_paths.size())
			Util.writeFile("}"+"\n", output_filename, true);
	}
   	String[] ASTNodeBigrams = null;
	ASTNodeBigrams= getASTNodeBigrams(dirPath);
	
	for (int i=0; i<ASTNodeBigrams.length; i++)	
    	
	  {  	ASTNodeBigrams[i] = ASTNodeBigrams[i].replace("'", "apostrophesymbol");
	    	Util.writeFile("@attribute 'ASTNodeBigramsTF "+i+"=["+ASTNodeBigrams[i]+"]' numeric"+"\n", output_filename, true);}
	Util.writeFile("@attribute 'authorName' {",output_filename, true);
	for(int i=0; i< test_file_paths.size(); i++){
		int testIDlength = test_file_paths.get(i).toString().length();   
		File authorFileName= new File(test_file_paths.get(i).toString());
		String authorName= authorFileName.getParentFile().getName();

		text = text.concat(authorName + ",");  
		String[] words = text.split( ",");
		  Set<String> uniqueWords = new HashSet<String>();

		   for (String word : words) {
		       uniqueWords.add(word);
		   }
		   words = uniqueWords.toArray(new String[0]);
		   int authorCount = words.length;
		   if (i+1==test_file_paths.size()){
		   for (int j=0; j< authorCount; j++){
			   {System.out.println(words[j]);
				if(j+1 == authorCount)
				{
			   Util.writeFile(words[j]+"}"+"\n\n",output_filename, true);
				}
				else
				{
				Util.writeFile(words[j]+","+"",output_filename, true);

					}
				}
			   }

		   }
		   
		 }
	Util.writeFile("@data"+"\n", output_filename, true);	
	//Finished defining the attributes
	
	//EXTRACT LABELED FEATURES
   	for(int i=0; i< test_file_paths.size(); i++){
		int testIDlength = test_file_paths.get(i).toString().length(); 
		File authorFileName= new File(test_file_paths.get(i).toString());
		String authorName= authorFileName.getParentFile().getName();

		System.out.println(test_file_paths.get(i));
		System.out.println(authorName);
		
		File fileCPPID = new File(test_cpp_paths.get(i).toString());
		String fileNameID = fileCPPID.getName();
		Util.writeFile(fileNameID+",", output_filename, true);

		String DepASTText = Util.readFile(test_file_paths.get(i).toString().substring(0,testIDlength-3)+"dep");
		  float[] typeCount = getASTNodeBigramsTF(DepASTText, ASTNodeBigrams );
		    for (int j=0; j<ASTNodeBigrams.length; j++)
			{Util.writeFile(typeCount[j] +",", output_filename, true);}	
		  
		
	  
		Util.writeFile(authorName+"\n", output_filename, true);

		
		
		
   	}
    	
}
    
    //dns43: returns an array of all unique subsequent unigrams
    //dns43: found in every very first dep line of a function <- why only that line
    //dns43: parameter is the path "../astfiles/" where .dep, .ast, .js, .txt are stored
    public static String [] getASTNodeBigrams(String dirPath) throws IOException{
     
        //dns43: test_file_paths holds paths to all .dep files in that specific folder (LIST in case author has multiple programs)
        List test_file_paths = Util.listDepFiles(dirPath);
        //dns43: uniqueWords is never used
	Set<String> uniqueWords = new LinkedHashSet<String>();
	List<String> unigrams = new ArrayList<String>();
	Set<String> bigrams = new LinkedHashSet<String>();
	String[] uniquebigrams = null;
	
        for(int i=0; i< test_file_paths.size(); i++){
            String filePath = test_file_paths.get(i).toString();

            String inputText =Util.readFile(filePath);
            //dns43: lines = {line of first function ID, line of second function ID, line of ith functionID,,,}
            //dns43: not needed for JS, because whole AST file can be used
            int [] lines = DepthASTNode.getASTDepLines(inputText);
            String textAST=null;


            for (int j=0; j<lines.length; j++)
            {
                    textAST = DepthASTNode.readLineNumber(inputText, lines[j]);
                    //dns43: replace functioniID, Tabulators and Paranthesis in each FIRST function line
                    String inputTextParanthesisRemoved = textAST.replaceAll("[()]"," ");
                    inputTextParanthesisRemoved = inputTextParanthesisRemoved.replaceAll("\\d+\\t"," ");
                    inputTextParanthesisRemoved = inputTextParanthesisRemoved.replaceAll("( )+"," ");

                    //	System.out.println(inputTextParanthesisRemoved);

                    //   Pattern pattern = Pattern.compile("([\\w']+)");
                    //dns43: stores each leftover "word/expression"  to List Unigrams and LinkedHashSet UniqueWords
                    Pattern pattern = Pattern.compile("(\\w+)\\s+");
                    Matcher matcher = pattern.matcher(inputTextParanthesisRemoved);
                    while (matcher.find()) {
                        unigrams.add(matcher.group());
                    }
                    while (matcher.find()) {
                        //dns43: store group 1 to LinkedHashSet
                        //dns43: this is never used
                        uniqueWords.add(matcher.group(1));
                    }
            }
        }
        
 //   String[] words = uniqueWords.toArray(new String[0]);
        //dns43: iterate over unigrams
	for(int i=1; i<unigrams.size(); i++){
	   //   System.out.println( unigrams.get(i-1));
            //dns43: get two subsequent unigrams and write them to a LinkedHashset
            //dnsr43: writing them to a hashset assures they are unique
            bigrams.add(unigrams.get(i-1).trim() + " "+unigrams.get(i).trim());
            //dns43: convert to a String
            uniquebigrams = bigrams.toArray(new String[bigrams.size()]);
	}	
        //dns43: return unique bigrams whoop whoop
    return uniquebigrams;
    }
    

    //dns43: returns an array of all unique subsequent unigrams
    //dns43: found in every very first dep line of a function <- why only that line
    //dns43: parameter is the path "../astfiles/" where .dep, .ast, .js, .txt are stored
    public static String [] getJSASTNodeBigrams(String dirPath) throws IOException{
     
        //dns43: test_file_paths holds paths to all .dep files in that specific folder (LIST in case author has multiple programs)
        List test_file_paths = Util.listDepFiles(dirPath);
        //dns43: uniqueWords is never used
	List<String> unigrams = new ArrayList<String>();
	Set<String> bigrams = new LinkedHashSet<String>();
	String[] uniquebigrams = null;
	
          //JS Paranthesises      
//        {
//            "type": "Punctuator",
//            "value": "{"
//        },
//       {
//            "type": "Punctuator",
//            "value": "}"
//        },                
//        {
//            "type": "Punctuator",
//            "value": "("
//        },    
//        {
//            "type": "Punctuator",
//            "value": ")"
//        },
//                {
//            "type": "Punctuator",
//            "value": "["
//        },
//                {
//            "type": "Punctuator",
//            "value": "]"
//        },
//        {
//            "type": "Punctuator",
//            "value": ";"
//        },

	    for(int i=0; i< test_file_paths.size(); i++){
                    String filePath = test_file_paths.get(i).toString();  
                    //dns43: testing purpose; we shoud discuss if we use file types .ast/.dep or .txt
                    filePath = "C:\\Users\\dns43\\Documents\\NetBeansProjects\\CodeStylometry\\testJS\\js_ast.txt";
                    String inputText =Util.readFile(filePath);
                    String[] lines = inputText.split("\n");
                    
                    for(int l = 0; l<lines.length; l++){
                        if(lines[l].contains("type")){
                              lines[l] = lines[l].replaceAll( " ","");
                              lines[l] = lines[l].replace("\"", "");
                              lines[l] = lines[l].replace(":", "");
                              lines[l] = lines[l].replace("type", "");
                              lines[l] = lines[l].replace(",", "");
                              unigrams.add(lines[l]);
                        }
                    }
                    for(int j = 1; j<unigrams.size(); j++){
                        bigrams.add(unigrams.get(j-1)+" "+unigrams.get(j));
                    }
	   }
       uniquebigrams = bigrams.toArray(new String[bigrams.size()]);
       return uniquebigrams;
}
    public static float [] getJSASTNodeBigramsTF (String featureText, String[] ASTNodeBigrams) throws IOException
    {
                    String[] lines = featureText.split("\n");
                    List<String> unigrams = new ArrayList<String>();
                    String unigramsS = null;
                    float [] counter = new float[ASTNodeBigrams.length];
                    
                    for(int l = 0; l<lines.length; l++){
                        if(lines[l].contains("type")){
                              lines[l] = lines[l].replaceAll( " ","");
                              lines[l] = lines[l].replace("\"", "");
                              lines[l] = lines[l].replace(":", "");
                              lines[l] = lines[l].replace("type", "");
                              lines[l] = lines[l].replace(",", "");
                              unigrams.add(lines[l]);
                        }
                    }
                    //unigramsS = unigrams.toArray(new String[unigrams.size()]).toString();
                    //String u = unigrams.toString().replaceAll("\[","");
                    //u = unigrams.toString().replaceAll("\]","");
                   String u ="";
                    for(int i = 0; i< unigrams.size(); i++){
                        u = u.concat(" "+unigrams.get(i));
                    }
                    //for(int j = 1; j<unigrams.size(); j++){
                        for(int i = 0; i<ASTNodeBigrams.length;i++){
                        counter[i] = StringUtils.countMatches(u, ASTNodeBigrams[i]);
                      //  }
                        //System.out.println(ASTNodeBigrams[j] + " appearance: "+counter[j]); 
                    }
	return counter;
   
    }
    
    
    /*
    public static float [] getJSASTNodeBigramsTF (String featureText, String[] ASTNodeBigrams) throws IOException
    {
        float symbolCount = ASTNodeBigrams.length;
        float [] counter = new float[(int) symbolCount];
    	String[] lines = featureText.split("\n");
                    for(int l = 0; l<lines.length; l++){
                        if(lines[l].contains("type")){
                              lines[l] = lines[l].replaceAll( " ","");
                              lines[l] = lines[l].replace("\"", "");
                              lines[l] = lines[l].replace(":", "");
                              lines[l] = lines[l].replace("type", "");
                              lines[l] = lines[l].replace(",", "");
                              //unigrams.add(lines[l]);
                        
                        for (int i=0; i<symbolCount; i++){
				//    	featureText remove paranthesis and replace with one space for feature text
				String str = ASTNodeBigrams[i].toString();
				//if case insensitive, make lowercase
				//   strcounter = StringUtils.countMatches(featureText.toLowerCase(), str);
				counter[i] = StringUtils.countMatches(lines[l], str); 
                                System.out.println(ASTNodeBigrams[i] + " appearance: "+counter[i]);
                    }
                        }
                    }
                    
	    return counter;
    }
    */
    public static float [] getASTNodeBigramsTF (String featureText, String[] ASTNodeBigrams ) throws IOException
    {    
    	float symbolCount = ASTNodeBigrams.length;
        float [] counter = new float[(int) symbolCount];
    	int [] lines = DepthASTNode.getASTDepLines(featureText);
		String textAST=null;
		for (int j=0; j<lines.length; j++)
		{
			textAST = DepthASTNode.readLineNumber(featureText, lines[j]);
			String inputTextParanthesisRemoved = textAST.replaceAll("[()]"," ");
			 inputTextParanthesisRemoved = inputTextParanthesisRemoved.replaceAll("\\d+\\t"," ");
			 inputTextParanthesisRemoved = inputTextParanthesisRemoved.replaceAll("( )+"," ");

			
			for (int i=0; i<symbolCount; i++){
				//    	featureText remove paranthesis and replace with one space for feature text
				String str = ASTNodeBigrams[i].toString();
				//if case insensitive, make lowercase
				//   strcounter = StringUtils.countMatches(featureText.toLowerCase(), str);
				counter[i] = StringUtils.countMatches(inputTextParanthesisRemoved, str);
    }
    }  
	    return counter;

}}
