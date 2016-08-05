# WSD(Wideband Single-bit Data) File Specification

Version 1.1 (English Version 0.1)

Specifications for 1-bit-coding Data File

January 20121 bit Audio Consortium

## Forward

NOTE 1: This is an unofficial English version of WSD File Specification Version 1.1 in Japanese.

NOTE 2: The copyright holder One-bit Audio Consortium, a private organization, was disbanded. 

ANY EXPRESSED OR IMPLIED WARRANTIES ARE DISCLAIMED. IN NO EVENT SHALL THE ONE-BIT CONSORTIUM BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, OR CONSEQUENTIAL DAMAGES ARISING IN ANY WAY OUT OF THE USE OF THIS SPECIFICATION, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

## 1. Scope

This specification defines file format to store a 1-bit coded data stream, which must be treated as a whole , into a storage media. The type of 1-bit coded data stream should be audio (music or tune) but future specification may extend use case of 1-bit coded data to other kind.

## 1.1 Overview

WSD format defines four data areas, shown in following clauses, in a logial storage unit or a file. Name of the file shall have an extenstion "wsd", for instance "xxxxxx.wsd". Every single WSD format file should contain one title of music.
### <1st area> General Information
This data area provides version information of this specification.### <2nd area> Data Specifications Information
This data area provides information of the 1-bit coded data, here after 'Stream Data', stored in the 4th data area. Playback systems shall read this information to determine method of data reproduction.

Informative: Any packetization or framing methods which may have header, are out of scope of this specification.
### <3rd area> Text Data
This data area provides textual information of the title, recorded day, location measured by GPS and so on.### <4th area> Stream Data
This data area contains 1-bit coded data for a single music. It shall contain no audio gap.

## 1.2 Future extensions and version control

The version of this specification is 1.1.
In this specification the Stream Data shall be a music.

This specificatoin defines two kinds of reserved field for future extention.
### (1) Reserved<sup>*1</sup>The reserved field specified by Reserved<sup>*1</sup> shall be used for the purposes defined in Tabal-1.
### (2) Reserved<sup>*2</sup>The reserved field specified by Reserved<sup>*2</sup> shall be used for textual information. These fields shall be filled up with '20h'.
### (3) Other extensions 
In addition to these reserved fields, expansion of data areas is possible by changing pointer value such as Text_SP and Data_SP. In this case specification version shall be 2.0 or greater;


## 2. Specification

This specification uses number notation as follows:

- 'Xb' denotes binary number where 'X' is a sequence of one or zero, for example '1101'.
- 'Xh' denote hexadecimal number where 'X' is a sequence of character in '0123456789ABCDEF'.
-  Otherwise 'X' is decimal number unless otherwise noted.

Data is stored in big-endian byte order.### 2.1 File structureFile structure is shown in Table-1NOTE: 'RBP' stands for 'Relative Byte Position' and  starts from zero.
<table border="1">
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

### 2.2 Area for the General Information and Data Spec. InformationAll bits of the field in Table-1 denoted by Reserved<sup>*1</sup> shall be '0b'.
 #### <RBP 0 to 3> File_ID"1bit" by using ASCII character set.
#### <RBP 8> Version_NTwo-digit version number is stored in BCD(Binary-coded Decimal). For example,

- ‘09h’: Version 0.9- ‘10h’: Version 1.0- ‘11h’: Version 1.1This specification shall have '11h' (Version 1.1) in this field.
#### <RBP 12 to 19> File_SZTotal file size (General Information + Data Spec. Information + Text Data + Stream Data) in byte.The data shall be stored as follows.

Lower 4bytes in <RBP 12 to 15>
Upper 4bytes in <RBP 16 to 19>

If the file size does not exceed 4GB

‘0000 0000 0000 0000 0000 0000 0000 0000b’ in <RBP 16 to 19>#### <RBP 20 to 23> Text_SPRelative byte position (RBP) to the Text Data area.
This field shall be '00000080h' for all specifications version 1.X (X=0,1,2...9).
#### <RBP 24 to 27> Data_SPRelative byte position (RBP) to the Stream Data area.
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

#### <RBP 36 to 39> fsSampling frequency of every channel of the Stream Data shall be identical to the value denoted by these fields.

In WSD format specificaiton Verion 1.0 only two sampling frequncy

- ‘0000 0000 0001 0101 1000 1000 1000 0000b’: 1411200Hz
- ‘0000 0000 0010 1011 0001 0001 0000 0000b’: 2822400Hz 

are defined.

This version 1.1 and later allows any samplig frequency.
#### <RBP 44> Ch_N

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
### <RBP 48 to 51> Ch_AsnThis field defines speaker location (channel assignment) for each channel in the Stream Data.
<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr style="border:0">
<td style="border:0"><strong>RBP48</strong></td><td>Reserved</td><td>Lf</td><td>Lf-middle</td><td>Cf</td><td>Rf-middle</td><td>Rf</td><td>Reserved</td><td>LFE
</tr><tr style="border:0"><td style="border:0"><strong>RBP49</strong></td><td colspan="8" align="center">Reserved</tr><tr style="border:0">
<td style="border:0"><strong>RBP50</strong></td><td colspan="8" align="center">Reserved</tr><tr style="border:0">
<td style="border:0"><strong>RBP51</strong></td><td>Reserved</td><td>Lr</td><td>Lr-middle</td><td>Cr</td><td>Rr-middle</td><td>Rr</td><td>Reserved</td><td>Reserved
</tr></table>RBP48 denotes front speakers, RBP51 denotes rear speakers. RBP49 and RBP50 are reserved for side speakers.

Each bit of these fields is set to '1b' if the designated channel exists in the Stream Data otherwise set to '0b'. All reserved bits shall be set to '0b'.

The total number of bit equals to '1b' in the Ch_Asn field shall be identical to the value of Ch_N field if Ch_N is greater or equal to 2.

The former version (1.0) defines two cases as follows.1) In case of Ch_N =‘0010b (2ch stereo)’ LF and Rf are set to '1b'.

2) In case of Ch_N = ‘0100b (4ch)’ Lf,Rf,Lr and Rr are set to '1b'.

This version 1.1 defines

1) In case of Ch_N = ‘0001b(1ch)’ and no specific speaker location  All bits of Ch_Asn shall be ‘0b’.
2) In case of Ch_N = ‘0001b(1ch)’ as Multiple Mono only one bif of CH_Asn shall be set to '1b'. For example, in Dual Mono use case one pair of WSD files shall be provided.
3) In case of Ch_N = ‘0110b(surround 5.1ch)’ Lf, Cf, Rf , LFE, Lr and Rr bits are set to '1b'.
NOTE 1: RBP 52 to 63 are reserved for speaker location.

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
<td style="border:0"><strong>RBP48</strong></td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1
</td>
</tr><tr style="border:0"><td style="border:0"><strong>RBP49</strong></td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>1</td><td>0
</td></tr><tr style="border:0">
<td style="border:0"><strong>RBP50</strong></td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>1</td><td>0
</td></tr><tr style="border:0">
<td style="border:0"><strong>RBP51</strong></td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1</td><td>1
</tr></table>
### <RBP 64 to 67> EmphNo emphasis: '00000000h’.
#### <RBP 72 to 87> Time ReferenceRecording start time expressed in number of samples.
### 2.3 Text Data areaIn this Text Data area a set of ASCII characters specified in following table shall be used.

|||
|:--|:--||alphabet|'A'&ndash;'Z', 'a'&ndash;'z'||space |'&nbsp;' (20h)||digit|'0'&ndash;'9'||symol|'!', '"', '#', '$', '%', '&', ''', '(', ')', '*', '+', ',', '-', '.', '/', ':', ';', '&lt;', '=', '&gt;', '?', '@', '[', '\', ']', '^','_', '`', '{', '&verbar;', '}', '~'|

The Text Data area is allocated to fields for specific information defined in this clause.
Texual information of a field, including empty, shall be stored at the location specified RBP and filled up rest of the field with space '20h' character.

Decives designed to support this specification shoud display at least first 16 characters in Title Name field if the device has text display function.

NOTE: Extension of the character set shall be done in version 2.0 or greater.

<!--• 各フィールドのデータは必ずフィールドのファーストポインタより埋め,先頭に余分な領域を作らないこと。• 各フィールドで使用しない領域(テキストデータを記述し、残った領域)は,全てスペース' '(16 進表記:20h)で埋めること。• 一部または全テキストデータの省略(未記入)を可能とする。ただし、その際は、各該当フィールドすべてをスペース' '(16 表記:20h)で埋めること。
-->
<!--• 再生機器におけるこれらの表示対応の有無はそれぞれの再生機器に委ねられる。• 再生機器においてこれらの表示を対応する場合,少なくとも Title Name の最初の 16byte が表示可能であること。(注)ASCII 以外のコードに対応する場合は Version2.0 以上で行うこととする。
-->
#### <RBP 128 to 255> Title NameTile of a piece of music or empty.#### <RBP 259 to 383> ComposerName of the composer or empty.#### <RBP 384 to 511> Song WriterName of the song writer or empty.#### <RBP 512 to 639> Artist NameName of the performer or empty.#### <RBP 640 to 767> Album NameName of album or empty.
#### <RBP 768 to 799> GenreGenre of the music or empty.
#### <RBP 800 to 831> Date & TimeRecording data and time or empty.

Dates of Gregorian calendar and time is expressed in 

YYYYMMDDhhmmss['02h''02h'{+|-}DDDD]where YYYY is four-digit year, MM is two-digit month, DD is two-digit day, hh is two-digit hour, mm is two-digit minute, ss is two-digit second followed by optional time zone information. The time zone is designated by four-digit number DDDD where two-digit hour and two-digit minute of GMT offset. 

#### <RBP 832 to 863> LocationTexual data of location of recording or latitude, longitude and altitude or emptry.

#### <RBP 864 to 1375> Comment

Textual data of comment by a file creator or empty.#### <RBP 1376 to 1887> User Specific

User defined textual data or empty. 
 
#### <RBP 1888 to 2047> Reserved<sup>*2</sup>

Reserved for textual data. This reserved field denoted by Reserved<sup>*2</sup> shall be filled up eith space character '20h'. Future specofocations (Ver.2.0 or later) may define another textual data area after this  
field.

### 2.3 Stream Data エリアEach channel 1-bit coded data (audio sample data) is aligned in a byte from MSB to LSB. And channel sequences of 8-bit aligned unit are interleaved to form the Stream Data.  

One cycle of channel-interleave consists of channels specified in Ch_Asn. The order of channel assignmnt look up is from MSB of RBP48 to LSB pf RBP51.

Each channel shall have identical number of 8-bit aligned units and the number shall be even.

In order to make the number even, at most 15 bits of sample of silence may to each channel data.  Extara padding data shall not ne appended. 
(Example 1) 2ch stereo (Lf, Rf) case:

<RBP 48 to 51> Ch_Asn has value show in the following table. (Lf and Rf only have ’1b’)
 
<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr style="border:0">
<td style="border:0"><strong>RBP48</strong></td><td>Reserved</td><td><strong>Lf</strong></td><td>Lf-middle</td><td>Cf</td><td>Rf-middle</td><td><strong>Rf</strong></td><td>Reserved</td><td>LFE
</tr><tr style="border:0"><td style="border:0"><strong>RBP49</strong></td><td colspan="8" align="center">Reserved</tr><tr style="border:0">
<td style="border:0"><strong>RBP50</strong></td><td colspan="8" align="center">Reserved</tr><tr style="border:0">
<td style="border:0"><strong>RBP51</strong></td><td>Reserved</td><td>Lr</td><td>Lr-middle</td><td>Cr</td><td>Rr-middle</td><td>Rr</td><td>Reserved</td><td>Reserved
</tr></table>According to the Ch_Asn, cycles of channel-interleave are as follows.

<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr><td rowspan="2"> Cycle#1
</td><td>Lf#0</td><td>Lf#1</td><td>Lf#2</td><td>Lf#3</td><td>Lf#4</td><td>Lf#5</td><td>Lf#6</td><td>Lf#7
</td>
</tr><tr><td>Rf#0</td><td>Rf#1</td><td>Rf#2</td><td>Rf#3</td><td>Rf#4</td><td>Rf#5</td><td>Rf#6</td><td>Rf#7</td>
</tr><tr><td rowspan="2"> Cycle#2</td><td>Lf#8</td><td>Lf#9</td><td>Lf#10</td><td>Lf#11</td><td>Lf#12</td><td>Lf#13</td><td>Lf#14</td><td>Lf#15
</td>
</tr><tr></td><td>Rf#8</td><td>Rf#9</td><td>Rf#10</td><td>Rf#11</td><td>Rf#12</td><td>Rf#13</td><td>Rf#14</td><td>Rf#15
</td>
</tr>
<tr>
<td colspan="9" align="center">...</td></tr>
<tr><td rowspan="2"> Cycle#n</td><td>Lf#8(n-1)</td><td>Lf#8(n-1)+1</td><td>Lf#8(n-1)+2</td><td>Lf#8(n-1)+3</td><td>Lf#8(n-1)+4</td><td>Lf#8(n-1)+5</td><td>Lf#8(n-1)+6</td><td>Lf#8(n-1)+7
</td>
</tr><tr></td><td>Rf#8(n-1)</td><td>Rf#8(n-1)+1</td><td>Rf#8(n-1)+2</td><td>Rf#8(n-1)+3</td><td>Rf#8(n-1)+4</td><td>Rf#8(n-1)+5</td><td>Rf#8(n-1)+6</td><td>Rf#8(n-1)+7
</td>
</tr></table>

NOTE: Lf#n denotes sample number #n of front L channel data.

(Example 2) 4ch(Lf,Rf,Lr,Rr) case:
 <RBP 48 to 51> Ch_Asn has value show in the following table. (Lf, Rf, Lr, Rr only have ’1b’)
<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr style="border:0">
<td style="border:0"><Strong>PBP48</Strong></td><td>Reserved</td><td><strong>Lf</strong></td><td>Lf-middle</td><td>Cf</td><td>Rf-middle</td><td><strong>Rf</strong></td><td>Reserved</td><td>LFE
</tr><tr style="border:0"><td style="border:0"><Strong>PBP49</Strong></td><td colspan="8" align="center">Reserved</tr><tr style="border:0"><td style="border:0"><Strong>PBP50</Strong></td><td colspan="8" align="center">Reserved</tr><tr style="border:0"><td style="border:0"><Strong>PBP51</Strong></td><td>Reserved</td><td><strong>Lr</strong></td><td>Lr-middle</td><td>Cr</td><td>Rr-middle</td><td><Strong>Rr</strong></td><td>Reserved</td><td>Reserved
</tr>
</table>

According to the Ch_Asn, cycles of channel-interleave are as follows.

<table>
<tr style="border:0">
<td style="border:0"></td><td style="border:0">MSB</td>
<td colspan="6" style="border:0"></td>
<td style="border:0">LSB</td>
</tr>
<tr> <td rowspan="4"> Cycle#1
</td><td>Lf#0
</td><td>Lf#1</td><td>Lf#2</td><td>Lf#3</td><td>Lf#4</td><td>Lf#5</td><td>Lf#6</td><td>Lf#7
</tr><tr></td><td>Rf#0</td><td>Rf#1</td><td>Rf#2</td><td>Rf#3</td><td>Rf#4</td><td>Rf#5</td><td>Rf#6</td><td>Rf#7
</tr><tr></td><td>Lr#0</td><td>Lr#1</td><td>Lr#2</td><td>Lr#3</td><td>Lr#4</td><td>Lr#5</td><td>Lr#6</td><td>Lr#7</tr><tr></td><td>Rr#0</td><td>Rr#1</td><td>Rr#2</td><td>Rr#3</td><td>Rr#4</td><td>Rr#5</td><td>Rr#6</td><td>Rr#7</tr><tr></td><td rowspan="4"> Cycle#2</td><td>Lf#8</td><td>Lf#9</td><td>Lf#10</td><td>Lf#11</td><td>Lf#12</td><td>Lf#13</td><td>Lf#14</td><td>Lf#15
</tr><tr></td><td>Rf#8</td><td>Rf#9</td><td>Rf#10</td><td>Rf#11</td><td>Rf#12</td><td>Rf#13</td><td>Rf#14</td><td>Rf#15
</tr><tr></td><td>Lr#8</td><td>Lr#9</td><td>Lr#10</td><td>Lr#11</td><td>Lr#12
</td><td>Lr#13</td><td>Lr#14</td><td>Lr#15
</tr><tr></td><td>Rr#8</td><td>Rr#9</td><td>Rr#10</td><td>Rr#11</td><td>Rr#12</td><td>Rr#13</td><td>Rr#14</td><td>Rr#15
</tr><tr>
<tr>
<td colspan="9" align="center">...</td></tr></td><td rowspan="4">Cycle#n</td><td>Lf#8(n-1)</td><td>Lf#8(n-1)+1</td><td>Lf#8(n-1)+2</td><td>Lf#8(n-1)+3</td><td>Lf#8(n-1)+4</td><td>Lf#8(n-1)+5</td><td>Lf#8(n-1)+6</td><td>Lf#8(n-1)+7
</tr><tr></td><td>Rf#8(n-1)</td><td>Rf#8(n-1)+1</td><td>Rf#8(n-1)+2</td><td>Rf#8(n-1)+3</td><td>Rf#8(n-1)+4</td><td>Rf#8(n-1)+5</td><td>Rf#8(n-1)+6</td><td>Rf#8(n-1)+7
</tr><tr></td><td>Lr#8(n-1)</td><td>Lr#8(n-1)+1</td><td>Lr#8(n-1)+2</td><td>Lr#8(n-1)+3</td><td>Lr#8(n-1)+4</td><td>Lr#8(n-1)+5</td><td>Lr#8(n-1)+6</td><td>Lr#8(n-1)+7
</tr><tr></td><td>Rr#8(n-1)</td><td>Rr#8(n-1)+1</td><td>Rr#8(n-1)+2</td><td>Rr#8(n-1)+3</td><td>Rr#8(n-1)+4</td><td>Rr#8(n-1)+5</td><td>Rr#8(n-1)+6</td><td>Rr#8(n-1)+7
</td>
</tr><tr></table>
