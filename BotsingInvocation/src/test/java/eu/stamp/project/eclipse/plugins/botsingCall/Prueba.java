package eu.stamp.project.eclipse.plugins.botsingCall;

public class Prueba {

	public static void main(String[] args) {
         
		/*
		 String sr1 = "AA/BB/CC";
          sr1 = sr1.replaceAll("/",".");
          System.out.println(sr1);
          
          String sr2 = "AA\\BB\\CC";
          System.out.println(sr2);
          sr2 = sr2.replaceAll("\\\\",".");
          System.out.println(sr2);
          
          String sr3 = "AA.bb";
          sr3 = sr3.replaceAll(".bb","");
          System.out.println(sr3);
          */
		/*
		String[] sr4 = new String[] {".AA.BB.CC","AA.BB"};
		for(int i = 0; i < sr4.length; i++) if(sr4[i].startsWith(".")) {
			sr4[i] = sr4[i].replaceFirst(".","");
		}
		System.out.println(sr4.toString());*/
		/*
		String sr5 = "  A B      C   D";
		while(sr5.contains("  ")) {
			sr5 = sr5.replaceFirst("  "," ");
		}
		System.out.println(sr5);*/
		String sr6 = "/AA.BB\\CC.";
		System.out.println(sr6);
		sr6 = sr6.replaceAll("\\\\",".").replaceAll("/",".");
		System.out.println(sr6);
		if(sr6.startsWith(".")) sr6 = sr6.replaceFirst(".","");
		System.out.println(sr6);
		if(sr6.endsWith(".")) sr6 = sr6.substring(0,sr6.length()-1);
		System.out.println(sr6);
	}
}
