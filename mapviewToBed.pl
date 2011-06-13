#!/usr/bin/perl

###### mapviewToBed.pl
## Author: APB
## January 27, 2009
##
## Convert maq mapview output to bed format.
##  (http://genome.ucsc.edu/FAQ/FAQformat#format1)
##
## This will output all sequence reads that are < MAX_HITS with 0 or 1
## mismatches and a mapping quality score above MIN_QUAL.
##
######

#------------------------------------------------------------------------------
# Usage requires input directory as argument
#
$usage = qq(
Usage:  $0 <MIN QUAL> <MAX_HITS> <FILE>
\tMIN_QUAL\t= Exclude alignments with <= this mapping quality score.
\tMAX_HITS\t= Exclude alignments with > this number of hits.
\tFILE\t\t= Mapview file to convert.
\n);

die($usage) if (@ARGV < 3);
#------------------------------------------------------------------------------

use strict;

my $MIN_QUAL = $ARGV[0]; #inclusive
my $MAX_HITS = $ARGV[1]; #non-inclusive
my $FILE = $ARGV[2];

open(MAPV, $FILE) or die("Could not open file $FILE!");
while(my $line = <MAPV>) {
    chomp($line);
    my @data = split('\t', $line);
    if($data[6] >= $MIN_QUAL) { #Best hit has acceptable mapping quality 
        if($data[9] == 0) {     #Best hit has 0 mismatches
            if($data[11] <= $MAX_HITS) {
                print print_bed(\@data);
            }
        } elsif($data[9] == 1) {    #Best hit has 1 mismatch
            if($data[12] <= $MAX_HITS) {
                print print_bed(\@data);
            }
        } else {        #best hit has > 1 mismatches
            #do nothing
        }
    }
}
close(MAPV);
exit(0);

sub print_bed {
    my($arr_ptr) = @_;
    my @toprint = @$arr_ptr;

    my $start;
    my $end;
	$start = $toprint[2];
	$end = $toprint[2] + $toprint[13];

    my $return_str = "";

    #correct maq is 1 based, UCSC is 0 based
    $start--;
    $end--;

    if($start < 0) {
        $start = 0;
    }
    $return_str .= $toprint[1]; #chrom
    $return_str .= "\t";
    $return_str .= $start; #start
    $return_str .= "\t";
    $return_str .= $end; #end
    $return_str .= "\t";
    $return_str .= $toprint[0]; #name
    $return_str .= "\t";
    $return_str .= $toprint[6]; #score
    $return_str .= "\t";
    $return_str .= $toprint[3]; #strand
    $return_str .= "\n";
    return $return_str;
}
