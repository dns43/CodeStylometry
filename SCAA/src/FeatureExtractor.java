import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

/**
 * FeatureExtractor writes extracted features to arff file to be used with WEKA
 * @author Aylin Caliskan-Islam (ac993@drexel.edu)
 */

public class FeatureExtractor {
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException {
		
		  String [] cppKeywords = {"alignas",	"alignof",	"and",	"and_eq",	"asm",	"auto",	
				  "bitand",	"bitor",	"bool",	"break",	"case",	"catch",	"char",	"char16_t",	"char32_t",
				  "class",	"compl",	"const",	"constexpr",	"const_cast",	"continue",	"decltype",	"default",	
				  "delete",	"do",	"double",	"dynamic_cast",	"else",	"enum",	"explicit",	"export",	
				  "extern",	"FALSE",	"float",	"for",	"friend",	"goto",	"if",	"inline",	"int",	"long",	
				  "mutable",	"namespace",	"new",	"noexcept",	"not",	"not_eq",	"nullptr",	"operator",	"or",
				  "or_eq"	,"private"	,"protected"	,"public"	,"register",	"reinterpret_cast",	"return",	
				  "short",	"signed",	"sizeof",	"static",	"static_assert",	"static_cast",	"struct",	
				  "switch",	"template",	"this"	,"thread_local",	"throw",	"TRUE",	"try",	"typedef",	"typeid",
				  "typename",	"union",	"unsigned",	"using",	"virtual",	"void",	"volatile",	"wchar_t",	"while",
				  "xor",	"xor_eq", "override", "final"};
		  
                   String [] JSKeywords = {"do", "if", "in", "for", "let", 
        "new", "try", "var", "case", "else", "enum", "eval", "this", "true", "void",
        "with", "await", "break", "catch", "class", "const", "false", "super", "throw",
        "while", "yield", "delete", "export", "import", "public", "return", 
        "static", "switch", "typeof", "default", "extends", "finally",
        "package", "private", "continue", "debugger", "function", "arguments",
        "interface", "protected", "implements", "instanceof", "await"};
        //dns43: delete comment to add older version's keywords
        //,"int", "byte", "goto", "long", "final", "float", "short", " double"
        //, " native", " throws", " boolean", " abstract", " volatile", " transient", "synchronized"
        //, " NaN", " Infinity", " undefined};
		  
		//Specifying the test arff filename
		Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
       	int month = cal.get(Calendar.MONTH);
       	int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
       	String time = sdf.format(cal.getTime());
    	//TODO when time changes, output_filename changes every time which needs to be corrected
//       	String output_filename = "/Users/Aylin/Desktop/Drexel/2014/ARLInternship/SCAAarffs/incremental/" +"CodeJam_14FilesPerAuthor_2014_"+ (month+1) + "." + 
//    	dayOfMonth + "_"+ time +".arff" ;
       	//for(int numberFiles=2; numberFiles<4; numberFiles++){
    	//dns43: replace those paths by my own ones
        //String output_filename = "/Users/Aylin/Desktop/Drexel/2014/ARLInternship/SCAAarffs/"
    	//		+ "mallory_150/SFS/" +"mallory_SFS_"+numberFiles+".arff" ;
        String output_filename = "C:\\Users\\dns43\\Documents\\NetBeansProjects\\CodeStylometry\\7authors4file\\CodeJam_4FilesPerAuthor.arff" ;
        
	      String test_dir ="C:\\Users\\dns43\\Documents\\NetBeansProjects\\CodeStylometry\\7authors4file\\";
       	List test_file_paths = Util.listTextFiles(test_dir);

	String text = "";
  	//Writing the test arff
  	//first specify relation
	//Util.writeFile("@relation "+numberFiles+"mallory_dataset_SFS_"+numberFiles+"\n"+"\n", output_filename, true);
	Util.writeFile("@relation CodeJam_4FilesPerAuthor\n"+"\n", output_filename, true);
	Util.writeFile("@attribute instanceID {", output_filename, true);
   	List test_cpp_paths = Util.listCPPFiles(test_dir);
   	for(int j=0; j < test_cpp_paths.size();j++ )
	{
		File fileCPP = new File(test_cpp_paths.get(j).toString());
		String fileName = fileCPP.getName();
		Util.writeFile(fileName+",", output_filename, true);
		if ((j+1)==test_cpp_paths.size())
			Util.writeFile("}"+"\n", output_filename, true);
	}

	Util.writeFile("@attribute 'functionIDCount' numeric"+"\n", output_filename, true);
	Util.writeFile("@attribute 'CFGNodeCount' numeric"+"\n", output_filename, true);
	Util.writeFile("@attribute 'ASTFunctionIDCount' numeric"+"\n", output_filename, true);
	Util.writeFile("@attribute 'getMaxDepthASTLeaf' numeric"+"\n", output_filename, true);

//	Util.writeFile("@attribute 'AverageASTDepth' numeric"+"\n", output_filename, true);

    //dns43: matches .txt and .doc files in test_dir unique patterns matching "u'(.*?)'", what ever that means
    String[] APIsymbols = FeatureCalculators.uniqueAPISymbols(test_dir);
    //uniqueASTTypes does not contain user input, such as function and variable names
    //uniqueDepASTTypes contain user input, such as function and variable names
    
//Use the following for syntactic inner nodes and code leaves (remember to change astlabel.py accordingly!
       //dns43: matches .dep files in test_dir for unique patterns matching regex "([\\w']+)"
       String[] ASTtypes = FeatureCalculators.uniqueJSDepASTTypes(test_dir);

       //dns43: matches .cpp files for everything that appears only once (whitespace as seperator)
       String[] wordUnigramsCPP =FeatureCalculators.wordUnigramsCPP(test_dir);
      //dns43: auskommentiert weil scheiße	
      //dns43: String Array filled with those Nodes that appear twice in the AST
      //dns43: e.g. the code has 2 if statements, so IF is a bigram
       String[] ASTNodeBigrams = BigramExtractor.getJSASTNodeBigrams(test_dir);

    //if only interested in syntactic features use this if the dep file contains user input    
 //   String[] ASTtypes =FeatureCalculators.uniqueASTTypes(test_dir);

    
//    for (int i=0; i<APIsymbols.length; i++)	
//    {	Util.writeFile("@attribute 'APIsymbols["+i+"]' numeric"+"\n", output_filename, true);}
    /*    for (int i=0; i<APIsymbols.length; i++)	
    {	Util.writeFile("@attribute 'APIsymbolsTFIDF["+i+"]' numeric"+"\n", output_filename, true);}
    */
//    for (int i=0; i<ASTtypes.length; i++)	
//    {	Util.writeFile("@attribute 'ASTtypes["+i+"]' numeric"+"\n", output_filename, true);}
/*    for (int i=0; i<ASTtypes.length; i++)	
    {	Util.writeFile("@attribute 'ASTtypesTFIDF["+i+"]' numeric"+"\n", output_filename, true);}
*/
        System.out.print(ASTNodeBigrams.length);
    	for (int i=0; i<ASTNodeBigrams.length; i++)		
 	  {  	ASTNodeBigrams[i] = ASTNodeBigrams[i].replace("'", "apostrophesymbol");
  	    	Util.writeFile("@attribute 'ASTNodeBigramsTF "+i+"=["+ASTNodeBigrams[i]+"]' numeric"+"\n", output_filename, true);}
      
    	for (int i=0; i<wordUnigramsCPP.length; i++)	   	
       {  	wordUnigramsCPP[i] = wordUnigramsCPP[i].replace("'", "apostrophesymbol");
            	Util.writeFile("@attribute 'wordUnigramsC "+i+"=["+wordUnigramsCPP[i]+"]' numeric"+"\n", output_filename, true);}

    	  
    for (int i=0; i<ASTtypes.length; i++)	
    	
  {  	ASTtypes[i] = ASTtypes[i].replace("'", "apostrophesymbol");
    	Util.writeFile("@attribute 'ASTNodeTypesTF "+i+"=["+ASTtypes[i]+"]' numeric"+"\n", output_filename, true);}
    for (int i=0; i<ASTtypes.length; i++)	
  {	    ASTtypes[i] = ASTtypes[i].replace("'", "apostrophesymbol");
    	Util.writeFile("@attribute 'ASTNodeTypesTFIDF "+i+"=["+ASTtypes[i]+"]' numeric"+"\n", output_filename, true);}
    for (int i=0; i<ASTtypes.length; i++)	
  {	    ASTtypes[i] = ASTtypes[i].replace("'", "apostrophesymbol");
    	Util.writeFile("@attribute 'ASTNodeTypeAvgDep "+i+"=["+ASTtypes[i]+"]' numeric"+"\n", output_filename, true);}
    /* //dns43: use JSKeywords instead
    for (int i=0; i<cppKeywords.length; i++)	
  {	Util.writeFile("@attribute 'cppKeyword "+i+"=["+cppKeywords[i]+"]' numeric"+"\n", output_filename, true);}
*/
    for (int i=0; i<JSKeywords.length; i++)	
  {	Util.writeFile("@attribute 'cppKeyword "+i+"=["+JSKeywords[i]+"]' numeric"+"\n", output_filename, true);}
    
    //dns43: till here the header wass written,
    //dns43: now the label:
    //dns43: here the authors for all of the files are written as once, as all paths are written in @InstanceID
    File authorFileName = null;
	//Writing the classes (authorname)
	Util.writeFile("@attribute 'authorName' {",output_filename, true);
        System.out.println("#testfiles:"+test_file_paths.size());
	for(int i=0; i < test_file_paths.size(); i++){
		int testIDlength = test_file_paths.get(i).toString().length();   
		authorFileName= new File(test_file_paths.get(i).toString());
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
                       {System.out.println("word "+j+": "+words[j]);
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
		String featureText = Util.readFile(test_file_paths.get(i).toString());
		int testIDlength = test_file_paths.get(i).toString().length(); 
		authorFileName= new File(test_file_paths.get(i).toString());
		String authorName= authorFileName.getParentFile().getName();

		System.out.println("i-th file: "+test_file_paths.get(i));
		System.out.println("authorname:"+authorName);
		File fileCPPID = new File(test_cpp_paths.get(i).toString());
		String fileNameID = fileCPPID.getName();
		Util.writeFile(fileNameID+",", output_filename, true);
		Util.writeFile(FeatureCalculators.functionIDJSCount(featureText)+",", output_filename, true);
		//dns43:C/C++ ast path replaced by JS ast path
                //String ASTText = Util.readFile(test_file_paths.get(i).toString().substring(0,testIDlength-3)+"ast");
                //String ASTText = Util.readFile("C:\\Users\\dns43\\Documents\\NetBeansProjects\\CodeStylometry\\testJS\\js_ast.txt");
                String ASTText = Util.readFile(test_file_paths.get(i).toString().substring(0,testIDlength-3)+"ast");
                //dns43: C/C++ dep path replaced by JS dep path
		//String DepASTText = Util.readFile(test_file_paths.get(i).toString().substring(0,testIDlength-3)+"dep");
                String DepASTText = Util.readFile(test_file_paths.get(i).toString().substring(0,testIDlength-3)+"ast");
                //String DepASTText = Util.readFile("C:\\Users\\dns43\\Documents\\NetBeansProjects\\CodeStylometry\\testJS\\js_ast.txt");
                //dns43: replace cpp with js
                //String sourceCode = Util.readFile(test_file_paths.get(i).toString().substring(0,testIDlength-3)+"cpp");
		//String sourceCode = Util.readFile("C:\\Users\\dns43\\Documents\\NetBeansProjects\\CodeStylometry\\testJS\\protobuf.js");
                String sourceCode = Util.readFile(test_file_paths.get(i).toString().substring(0,testIDlength-3)+"js");

		Util.writeFile(FeatureCalculators.CFGNodeCount(ASTText)+",", output_filename, true);
		Util.writeFile(FeatureCalculators.ASTFunctionIDCount(ASTText)+",", output_filename, true);
		Util.writeFile(DepthASTNode.getJSMaxDepthASTLeaf(ASTText, ASTtypes)+",", output_filename, true);
		
//		
		
		
//		Util.writeFile(FeatureCalculators.averageASTDepth(ASTText)+",", output_filename, true);


		//get count of each API symbol present	 
/*	    float[] symCount = FeatureCalculators.APISymbolTF(featureText, APIsymbols );
	    for (int j=0; j<APIsymbols.length; j++)
		{Util.writeFile(symCount[j]+",", output_filename, true);}	
*/
/*		//get tfidf of each API symbol present	 
	    float[] symTFIDF = FeatureCalculators.APISymbolTFIDF(featureText,test_dir, APIsymbols );
	    for (int j=0; j<APIsymbols.length; j++)
		{Util.writeFile(symTFIDF[j]+",", output_filename, true);}	 */

	    
/*	    //get count of each AST type present	 
	    float[] typeCount = FeatureCalculators.ASTTypeTF(ASTText, ASTtypes );
	    for (int j=0; j<ASTtypes.length; j++)
		{Util.writeFile(typeCount[j] +",", output_filename, true);}	*/
	    
/*		//get tfidf of each AST Type present	 
	    float[] astTypeTFIDF = FeatureCalculators.ASTTypeTFIDF(featureText, test_dir, ASTtypes);
	    for (int j=0; j<ASTtypes.length; j++)
		{Util.writeFile(astTypeTFIDF[j]+",", output_filename, true);}	*/

	    //get frequency of each ASTnodebigram in CPP source file's AST	
            //dns43: auskommentiert weil scheiße
		float[] bigramCount = BigramExtractor.getJSASTNodeBigramsTF(DepASTText, ASTNodeBigrams );
		for (int j=0; j<ASTNodeBigrams.length; j++)
		{Util.writeFile(bigramCount[j] +",", output_filename, true);}

	    //get count of each wordUnigram in CPP source file	 
	    float[] wordUniCount = FeatureCalculators.WordUnigramTF(sourceCode, wordUnigramsCPP);
	    for (int j=0; j<wordUniCount.length; j++)
		{Util.writeFile(wordUniCount[j] +",", output_filename, true);}	
	    
	    //get count of each ASTtype not-DepAST type present	 
	    float[] typeCount = FeatureCalculators.DepASTTypeTF(DepASTText, ASTtypes );
	    for (int j=0; j<ASTtypes.length; j++)
		{Util.writeFile(typeCount[j] +",", output_filename, true);}	
	    
		//get tfidf of each AST Type present	 
	    float[] DepastTypeTFIDF = FeatureCalculators.DepASTTypeTFIDF(DepASTText, test_dir, ASTtypes);
	    for (int j=0; j<ASTtypes.length; j++)
		{Util.writeFile(DepastTypeTFIDF[j]+",", output_filename, true);}	
		
    	float [] depFeature =DepthASTNode.getJSAvgDepthASTNode(DepASTText,ASTtypes);
    	for(int k=0;k<depFeature.length;k++)
		{Util.writeFile(depFeature[k] +",", output_filename, true);}	
	    
    	float [] cppKeywordsTF =FeatureCalculators.getJSKeywordsTF(sourceCode);
    	for(int k=0;k<cppKeywordsTF.length;k++)
		{Util.writeFile(cppKeywordsTF[k] +",", output_filename, true);}	
    	
    	
		Util.writeFile(authorName+"\n", output_filename, true);

   	
   			}
       	
   	}
   
	  
   	
	  public static String[]  uniqueDirectoryWords (String directoryFilePath){

		    String text = "FunctionName: operator"+ "";
		            
		            

		    Matcher m = Pattern.compile("(?m)^.*$").matcher(text);

		   
		    while (m.find()) {
		        System.out.println("line = " + m.group());
		        if(m.group().startsWith("Features (list):"));
		        
		    }
		  


	        while (m.find()) {
	            System.out.println("line = " + m.group());}
		  
		  String[] words = text.split( "\\s+");
		  Set<String> uniqueWords = new HashSet<String>();

		   for (String word : words) {
		       uniqueWords.add(word);
		   }
		   words = uniqueWords.toArray(new String[0]);
		   return words;
		 }	
}
	











