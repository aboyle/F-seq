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

Build with ant from the base directory.

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

-----------------------------------
License

Licensed under the GNU General Public License 3.0 license.

F-seq is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

F-seq is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with F-seq.  If not, see <http://www.gnu.org/licenses/>.
