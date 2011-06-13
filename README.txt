Welcome to F-Seq.

This software requires Java version 1.5 or greater.  
To see your version of java (or if it is installed), type 'java -version'

If java is not installed or you do not have the correct version, download at 
http://java.sun.com/javase/downloads/index.jsp

Current accepted input formats:
Bed, http://genome.ucsc.edu/FAQ/FAQformat#format1

Currently accepted output formats:
Wiggle, http://genome.ucsc.edu/FAQ/FAQformat#format6
Bed (see above)

-----------------------------------
Unix (Linux)

Make sure 'bin/fseq' is executable (chmod 0755 bin/fseq)

For a list of options, type 'fseq -h'

Example: fseq -v -of wig chr1.bed chr2.bed

This takes as input the chr1.bed and chr2.bed files. 
Will use verbose output and outputs the densities in the wiggle format.

-----------------------------------
Troubleshooting

A likely cause for errors is an "OutOfMemory" exception.  
To increase the available memory to the java virtual machine, edit
'bin/fseq' file and change the JAVA_OPTS property to increase the heap size.