# WSD(Wideband Single-bit Data) File Specification

Version 1.1 (English Version 0.1)

Specifications for 1-bit-coding Data File

January 2012

## Forward

NOTE 1: This is an unofficial English version of WSD File Specification Version 1.1 in Japanese.

NOTE 2: The copyright holder One-bit Audio Consortium, a private organization, was disbanded. 

ANY EXPRESSED OR IMPLIED WARRANTIES ARE DISCLAIMED. IN NO EVENT SHALL THE ONE-BIT CONSORTIUM BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY WAY OUT OF THE USE OF THIS SPECIFICATION, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## 1. Scope

This specification defines file format to store a 1-bit coded data stream, which must be treated as a whole , into a storage media. The type of 1-bit coded data stream should be audio (music or tune) but future specification may extend use case of 1-bit coded data to other kind.

## 1.1 Overview

WSD format defines four data areas, shown in following clauses, in a logial storage unit or a file. Name of the file shall have an extenstion "wsd", for instance "xxxxxx.wsd". Every single WSD format file should contain one title of music.




Informative: Any packetization or framing methods which may have header, are out of scope of this specification.




## 1.2 Future extensions and version control

The version of this specification is 1.1.
In this specification the Stream Data shall be a music.

This specificatoin defines two kinds of reserved field for future extention.



In addition to these reserved fields, expansion of data areas is possible by changing pointer value such as Text_SP and Data_SP. In this case specification version shall be 2.0 or greater;


## 2. Specification

This specification uses number notation as follows:

- 'Xb' denotes binary number where 'X' is a sequence of one or zero, for example '1101'.
- 'Xh' denote hexadecimal number where 'X' is a sequence of character in '0123456789ABCDEF'.
-  Otherwise 'X' is decimal number unless otherwise noted.

Data is stored in big-endian byte order.

<caption>
&lt;Table-1&gt;  Structure of ‘xxxxxxxx.wsd’ file
</caption>
<thead>
<tr>
<th>Area</th><th colspan="3">RBP</th><td>Field Name</th><th>Size:(bytes)</th><th>Description</th>
</tr>
</thead>
<tr>
<td rowspan="9">General Informataion</td>
<td>0</td><td>to</td><td>3</td>
<td>Field ID</td><td>4</td><td>Field Identifier "1bit"</td>
</tr>
<tr>
<td>4</td><td>to</td><td>7</td>
<td>Reserved<sup>*1</sup></td><td>4</td><td>reserved field(for File_ID)</td>
</tr>
<tr>
<td></td><td>8</td><td></td>
<td>Version_N</td><td>1</td><td>Version number</td>
</tr>
<tr>
<td></td><td>9</td><td></td>
<td>Reserved<sup>*1</td><td>1</td><td>reserved field</td>
</tr>
<tr>
<td>10</td><td>to</td><td>11</td>
<td>Reserved<sup>*1</td><td>2</td><td>reserved field</td>
</tr>
<tr>
<td>12</td><td>to</td><td>19</td>
<td>File_SZ</td><td>8</td><td>File size</td>
</tr>
<tr>
<td>20</td><td>to</td><td>23</td>
<td>Text_SP</td><td>4</td><td>Start address of the Text Data area</td>
</tr>
<tr>
<td>24</td><td>to</td><td>27</td>
<td>Data_SP</td><td>4</td><td>Start address of the Stream Data</td>
</tr>
<tr>
<td>28</td><td>to</td><td>31</td>
<td>Reserved<sup>*1</td><td>4</td><td>reserved field</td>
</tr>
<!---->
<tr>
<td rowspan="12">Data Spec. Informataion</td>
<td>32</td><td>to</td><td>35</td>
<td>PB_TM</td><td>4</td><td>Playback time</td>
</tr>
<tr>
<td>36</td><td>to</td><td>39</td>
<td>fs</td><td>4</td><td>Sampling frequency</td>
</tr>
<tr>
<td>40</td><td>to</td><td>43</td>
<td>Reserved<sup>*1</td><td>4</td><td>reserved field</td>
</tr>
<tr>
<td></td><td>44</td><td></td>
<td>Ch_N</td><td>1</td><td>Number of channels</td>
</tr>
<tr>
<td>45</td><td>to</td><td>47</td>
<td>Reserved<sup>*1</td><td>3</td><td>reserved field</td>
</tr>
<tr>
<td>48</td><td>to</td><td>51</td>
<td>Ch_Asn</td><td>4</td><td>Channel Assignment</td>
</tr>
<tr>
<td>52</td><td>to</td><td>63</td>
<td>Reserved<sup>*1</td><td>12</td><td>reserved field for speaker location</td>
</tr>
<tr>
<td>64</td><td>to</td><td>67</td>
<td>Emph</td><td>4</td><td>Emphasis</td>
</tr>
<tr>
<td>68</td><td>to</td><td>71</td>
<td>Reserved<sup>*1</td><td>4</td><td>reserved field</td>
</tr>
<tr>
<td>72</td><td>to</td><td>87</td>
<td>Time Reference</td><td>16</td><td>128bit:First count since midnight</td>
</tr>
<tr>
<td>88</td><td>to</td><td>127</td>
<td>Reserved<sup>*1</td><td>40</td><td>reserved field</td>
</tr>
<tr>
<td></td><td></td><td></td>
<td>Extension Area</td><td>Variable</td><td>NOTE: Ver.2.0 or later may define</td>
</tr>
<!---->
<tr>
<td rowspan="11">Text Data</td>
<td>128</td><td>to</td><td>255</td>
<td>Title Name</td><td>128</td><td>title</td>
</tr>
<tr>
<td>256</td><td>to</td><td>383</td>
<td>Composer</td><td>128</td><td>name of the composer</td>
</tr>
<tr>
<td>384</td><td>to</td><td>511</td>
<td>Song Writer</td><td>128</td><td>name of the song writer</td>
</tr>
<tr>
<td>512</td><td>to</td><td>639</td>
<td>Artist</td><td>128</td><td>name of the artist</td>
</tr>
<tr>
<td>640</td><td>to</td><td>767</td>
<td>Album</td><td>128</td><td>name of the album</td>
</tr>
<tr>
<td>768</td><td>to</td><td>799</td>
<td>Genre</td><td>32</td><td>genre</td>
</tr>
<tr>
<td>800</td><td>to</td><td>831</td>
<td>Date & Time</td><td>32</td><td>recorded data:yyyymmddhhmmss</td>
</tr>
<tr>
<td>832</td><td>to</td><td>836</td>
<td>Location</td><td>32</td><td>recorded place</td>
</tr>
<tr>
<td>864</td><td>to</td><td>1375</td>
<td>Comment</td><td>512</td><td>comment</td>
</tr>
<tr>
<td>1376</td><td>to</td><td>1887</td>
<td>User Specific</td><td>512</td><td>User specific data(eg:ISRC,JAN)</td>
</tr>
<tr>
<td>1888</td><td>to</td><td>2047</td>
<td>Reserved<sup>*1</td><td>160</td><td>reserved field</td>
</tr>
<!-->
<tr>
<td>Stream Data</td><td colspan="3">2048(pointed by Data_SP)</td><td>Stream Data</td><td>Variable</td><td>byte interleaved multi channel</td>
</tr>

</table>

### 2.2 Area for the General Information and Data Spec. Information
 


- ‘09h’: Version 0.9


Lower 4bytes in <RBP 12 to 15>
Upper 4bytes in <RBP 16 to 19>

If the file size does not exceed 4GB

‘0000 0000 0000 0000 0000 0000 0000 0000b’ in <RBP 16 to 19>
This field shall be '00000080h' for all specifications version 1.X (X=0,1,2...9).

This field shall be '00000800h' for all specifications version 1.X (X=0,1,2...9).

#### <RBP 32 to 35> PB_TM
Duration time of the Stream Data stored in BCD format.

<table>
<tr>
<td colspan="2">MSB RBP35</td>
<td colspan="2">RBP34</td>
<td colspan="2">RBP33</td>
<td colspan="2">RBP32 LSB</td>
</tr>
<tr>
<td>hour(10)</td><td>hour(1)</td>
<td>minute(10)</td><td>minute(1)</td>
<td>second(10)</td><td>second(1)</td>
<td>Reserved</td><td>Reserved</td>
</tr>
</table>

#### <RBP 36 to 39> fs

In WSD format specificaiton Verion 1.0 only two sampling frequncy

- ‘0000 0000 0001 0101 1000 1000 1000 0000b’: 1411200Hz
- ‘0000 0000 0010 1011 0001 0001 0000 0000b’: 2822400Hz 

are defined.

This version 1.1 and later allows any samplig frequency.


The numner of channles of the Stream Data shall be stored in lower 4 bits and upper 4 bits shall be filled up with '0b' as Reserverd<sup>*1</sup>.

In WSD format specificaiton Verion 1.0 only two cases

- 2ch (stereo)
- 4ch

are defined.

This version 1.1 defines

- 1ch (mono)
- 5ch
- 6ch

cases.

The number of channles denoted in this field shall include LFE channel. For example, the number of channel of 5.1ch format is 6.

<table>
<tr style="border:0">
<td style="border:0">
</td><td style="border:0">MSB
</td><td style="border:0" align="right">LSB
</tr>
<tr style="border:0">
<td style="border:0"><strong>RBP44</strong>
</td><td align="center">Reserved
</td><td align="center">Number of Channel
</td>
</tr>
</table>


<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr style="border:0">
<td style="border:0"><strong>RBP48</strong>
</tr><tr style="border:0">
<td style="border:0"><strong>RBP50</strong>
<td style="border:0"><strong>RBP51</strong>
</tr>

Each bit of these fields is set to '1b' if the designated channel exists in the Stream Data otherwise set to '0b'. All reserved bits shall be set to '0b'.

The total number of bit equals to '1b' in the Ch_Asn field shall be identical to the value of Ch_N field if Ch_N is greater or equal to 2.

The former version (1.0) defines two cases as follows.

2) In case of Ch_N = ‘0100b (4ch)’ Lf,Rf,Lr and Rr are set to '1b'.

This version 1.1 defines

1) In case of Ch_N = ‘0001b(1ch)’ and no specific speaker location  All bits of Ch_Asn shall be ‘0b’.




NOTE 2: Mapping of Ch_Asn bit field to location of channel in the Stream data is described in 'Stream Data' clause.

NOTE 3: (Informative) In case that speakes are located along a circle use of reserved bits is suggested as follows.

For example, 18.1ch circle location.

<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr style="border:0">
<td style="border:0"><strong>RBP48</strong>
</td>
</tr><tr style="border:0">
</td>
<td style="border:0"><strong>RBP50</strong>
</td>
<td style="border:0"><strong>RBP51</strong>
</tr>




|||
|:--|:--|

The Text Data area is allocated to fields for specific information defined in this clause.


Decives designed to support this specification shoud display at least first 16 characters in Title Name field if the device has text display function.

NOTE: Extension of the character set shall be done in version 2.0 or greater.

<!--
-->
<!--
-->

#### <RBP 768 to 799> Genre
#### <RBP 800 to 831> Date & Time

Dates of Gregorian calendar and time is expressed in 

YYYYMMDDhhmmss['02h''02h'{+|-}DDDD]

#### <RBP 832 to 863> Location

#### <RBP 864 to 1375> Comment

Textual data of comment by a file creator or empty.

User defined textual data or empty. 
 
#### <RBP 1888 to 2047> Reserved<sup>*2</sup>

Reserved for textual data. This reserved field denoted by Reserved<sup>*2</sup> shall be filled up eith space character '20h'. Future specofocations (Ver.2.0 or later) may define another textual data area after this  
field.

### 2.3 Stream Data エリア

One cycle of channel-interleave consists of channels specified in Ch_Asn. The order of channel assignmnt look up is from MSB of RBP48 to LSB pf RBP51.

Each channel shall have identical number of 8-bit aligned units and the number shall be even.

In order to make the number even, at most 15 bits of sample of silence may to each channel data.  Extara padding data shall not ne appended. 


<RBP 48 to 51> Ch_Asn has value show in the following table. (Lf and Rf only have ’1b’)
 
<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr style="border:0">
<td style="border:0"><strong>RBP48</strong>
</tr><tr style="border:0">
<td style="border:0"><strong>RBP50</strong>
<td style="border:0"><strong>RBP51</strong>
</tr>

<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr>
</td><td>Lf#0
</td>
</tr><tr>
</tr><tr>
</td>
</tr><tr>
</td>
</tr>
<tr>
<td colspan="9" align="center">...</td>
<tr>
</td>
</tr><tr>
</td>
</tr>

NOTE: Lf#n denotes sample number #n of front L channel data.

(Example 2) 4ch(Lf,Rf,Lr,Rr) case:

<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr style="border:0">
<td style="border:0"><Strong>PBP48</Strong>
</tr><tr style="border:0">
</tr>
</table>

According to the Ch_Asn, cycles of channel-interleave are as follows.

<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr> 
</td><td>Lf#0
</td><td>Lf#1
</tr><tr>
</tr><tr>
</tr><tr>
</tr><tr>
</td><td>Lr#13
</tr><tr>
</tr><tr>
<tr>
<td colspan="9" align="center">...</td>
</tr><tr>
</tr><tr>
</tr><tr>
</td>
</tr><tr>