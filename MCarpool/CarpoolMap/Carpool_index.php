<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>同行拼车平台</title>
<script type="text/javascript">

function checkDigits(value)
{
	if(value.length!=11){
		alert("Please enter your 11 digits membership ID");
		document.getElementById('membership_p5').value="Please input your ID Here";
	}
}
</script>

</head>

<body>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<p>&nbsp;</p>
<table width="400" border="1" align="center">
  <tr>
    <td colspan="2"><div align="center">&#27426;&#36814;&#36827;&#20837;&#21516;&#34892;&#25340;&#36710;&#32593;</div></td>
  </tr>
  <tr>
    <td colspan="2">&nbsp;</td>
  </tr>
  <tr>
    <td colspan="2"><div align="center">会员登陆
      <input type="button" name="Submit2" value="注册成为会员">
    </div></td>
  </tr>
  <tr>
    <td colspan="2"><form name="form1" method="post" action="Carpool_Driver.php">
        <p>会员号码:
          <input name="membership_p" type="text" id="membership_p5" style="background-color: #55cc33;" onchange="checkDigits(this.value)" size="20" maxlength="11">
          <input type="submit" name="Submit" value="登陆">
</p>
        <p>作为
          <input name="radiobutton" type="radio" value="radiobutton" checked>
  乘客
  <input type="radio" name="radiobutton" value="radiobutton">
  车主        </p>
        </form></td>
  </tr>
  <tr>
    <td colspan="2"><div align="center">非会员登陆</div></td>
  </tr>
  <tr>
    <td><form name="form3" method="post" action="Carpool_Passenger.php">
      <p align="center">
        <input name="Submit_P" type="submit" id="Submit_P5" value="我是乘客">      
        </p>
      </form></td>
    <td><form name="form2" method="post" action="Carpool_Driver.php">
      <div align="center">
        <input name="Submit_D" type="submit" id="Submit_D3" value="我是车主">    
        </div>
    </form></td>
  </tr>
</table>
<p>&nbsp;</p>
</body>
</html>
