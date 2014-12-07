<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>同行拼车平台-车主终端</title>
<script language="javascript" src="http://api.ditu.aliyun.com/map.js" ></script>
<script type="text/javascript">
	var map,marker,marker_from,marker_to;
	var label,label_from,label_to;
	var poiname,poiname_from,poiname_to;
	
	var g_lat,g_lng,g_zoom,g_latlng;
	var g_lat_from,g_lng_from,g_lat_to,g_lng_to;
	
	var g_driver_lat_from,	g_driver_lng_from,	g_driver_lat_to, g_driver_lng_to;
	var g_driver_angle,	g_driver_address_from,	g_driver_address_to;
	var g_driver_end_time,	g_driver_start_time,g_driver_name,g_driver_cellphone;

	var g_pass_lat_from,g_pass_lng_from,g_pass_address_from;
	var g_pass_lat_to,g_pass_lng_to,g_pass_address_to;
	var g_pass_angle;
	
	var points;
	var direction;
	var textOverlay_from,textOverlay_to;
	var timeout,tags={};
	var geocoder,oval;
	var $p_Name,$p_Lat_from,$p_Lng_from,$p_Lat_to,$p_Lng_to,$p_Angle; //passenger's information

	var pArray=new Array();
	var valueToPush=new Array();
	//var pArray=new Array();

	//var valueToPush={};
	//var valueToPush=new Array();
	
	Jla.require("Ali.Map.Ajax.Geocoder");//address 
	//Initial default position, should be replaced by GPS or default position
	g_lat=39.88891198123449;
	g_lng=116.36906311035156;
	g_zoom=12;
	
	
	//AliEvent.addListener(marker,"dragend",onDragEnd);
	
	function onload()
	{
	
		//Initial creat a Alimap object
		map=new AliMap("mapDiv"); //
		map.centerAndZoom(new AliLatLng(g_lat,g_lng),g_zoom);//显示地图
		
		oval=new AliOvalOverlay(null,{weight:1});
	    marker=new AliMarker(null,{weight:1});
		icon1=new (Jla.get("Ali.Map.Overlay.Marker.Icon.Icon2"))("green");
		icon2=new (Jla.get("Ali.Map.Overlay.Marker.Icon.Icon2"))("red");
			
		map.addOverlay(marker);
		map.addOverlay(oval);
		
		//创建事件监视器，在地图移动后执行onMapMove函数
		//AliEvent.addListener(map,"aftermove",onMapMove); 

		//捕获地图的点击事�?
		AliEvent.addListener(map,"click",onClick);

		//给地图添加滚轮缩放功�?
		//control=new AliMapMouseWheelControl();
		//map.addControl(control);

		//为地图添加右键菜单功能支�?
		map.addControl(new AliMapContextMenuControl());
		//添加几个右键菜单�?
		map.addContextMenuItem("center");
		//添加右键菜单分割�?
		map.addContextMenuLine();
		map.addContextMenuItem("zoomin");
		map.addContextMenuItem("zoomout");
		map.addContextMenuItem("zoomto");
		
		//设置地图的鼠标样式方�?
		setCursors('default','move','crosshair')

		//显示完整的导航控�?
		control=new AliMapLargeControl({hidePanBtn:false,hideZoomBtn:false,hideZoomBar:false})
		map.addControl(control);
		control.setPosition({x:10,y:10});

		//创建一个鹰眼控�?
		control=new AliMapOverviewControl({open:true});
		//将鹰眼控件添加到地图
		map.addControl(control);

		//创建一个比例尺控件
		control=new AliMapScaleControl()
		//将比例尺控件添加到地图
		map.addControl(control);

		geocoder=new (Jla.get("Ali.Map.Ajax.Geocoder"))();
	
		
	}
	
	//定义在地图移动后执行的函�?
	//此函数接收move事件传递的参数
	function onMapMove(center)
	{
		//g_lat=center.lat();
		//g_lng=center.lng();
		//g_zoom=map.getZoom();
		//document.getElementById("mLat").innerHTML=center.lat();
	   // document.getElementById("mLng").innerHTML=center.lng();
	   // document.getElementById("mZoom").innerHTML=map.getZoom();
		
	}
	
	//设置地图的鼠标样式方案，单个参数分别代表地图正常、移动、标点三种状态下的鼠标样�?
	function setCursors(cur_normal,cur_move,cur_mark)
	{
	    if(cur_normal){map.setCursor("normal",cur_normal)}
	    if(cur_move){map.setCursor("move",cur_move)}
	    if(cur_mark){map.setCursor("mark",cur_mark)}
	}
	//decide zoom by the labeled points
	//example: var points={new AliLatLng(30.238905,120.145583),	new AliLatLng(30.231114,120.137075)};
	
	function onBoundsZoom(points)
	{
		//获取这一系列坐标的范�?
		var bounds=AliBounds.getLatLngBounds(points);
		//将地图定位到显示此范围最合适的位置
		map.centerAndZoom(bounds.getCenter(),map.getBoundsZoom(bounds));
	}
	///*
	function trace_map()
	{
		//alert(g_pass_lat_from);
		map.clearOverlays();
		
		//get latlng of driver and passenger
		var pass_latlng_from=new AliLatLng(g_pass_lat_from,g_pass_lng_from);
		var pass_latlng_to=new AliLatLng(g_pass_lat_to,g_pass_lng_to);

		var driver_latlng_from=new AliLatLng(g_driver_lat_from,g_driver_lng_from);
		var driver_latlng_to=new AliLatLng(g_driver_lat_to,g_driver_lng_to);

		//define markers, pass
		marker_pass_from=new AliMarker(pass_latlng_from);
		marker_pass_to=new AliMarker(pass_latlng_to);
		
		
		textOverlay_pass_from=new AliPointOverlay(pass_latlng_from,'<div style="color:red;font-size:15px;">起点</div>',{offset:{x:-15,y:0}});
		textOverlay_pass_to=new AliPointOverlay(pass_latlng_to,'<div style="color:red;font-size:15px;">起点</div>',{offset:{x:-15,y:0}});

		label_pass_from=new AliPointOverlay(pass_latlng_from,g_pass_address_from,{style:{backgroundColor:'#0000FF',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
		label_pass_to=new AliPointOverlay(pass_latlng_to,g_pass_address_to,{style:{backgroundColor:'#0000FF',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});

		//define markers, pass
		marker_driver_from=new AliMarker(driver_latlng_from);
		marker_driver_to=new AliMarker(driver_latlng_to);

		textOverlay_driver_from=new AliPointOverlay(driver_latlng_from,'<div style="color:red;font-size:15px;">起点</div>',{offset:{x:-15,y:0}});
		textOverlay_driver_to=new AliPointOverlay(driver_latlng_to,'<div style="color:red;font-size:15px;">起点</div>',{offset:{x:-15,y:0}});

		label_driver_from=new AliPointOverlay(driver_latlng_from,g_driver_address_from,{style:{backgroundColor:'#0000FF',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
		label_driver_to=new AliPointOverlay(driver_latlng_to,g_driver_address_to,{style:{backgroundColor:'#0000FF',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
			  
		//put marker into map	
		
		//map.addOverlay(marker_pass_from);
		//map.addOverlay(textOverlay_pass_from);
		map.addOverlay(label_pass_from);

		//map.addOverlay(marker_pass_to);
		//map.addOverlay(textOverlay_pass_to);
		map.addOverlay(label_pass_to);
		//*/
		map.addOverlay(marker_driver_from);
		map.addOverlay(textOverlay_driver_from);
		map.addOverlay(label_driver_from);

		map.addOverlay(marker_driver_to);
		map.addOverlay(textOverlay_driver_to);
		map.addOverlay(label_driver_to);
		//get angle between passenger track and drivertrack
		//angle=Math.atan2((g_lng_to-g_lng_from),(g_lat_to-g_lat_from))/Math.PI*180;
		angle=Math.abs(g_driver_angle-g_pass_angle);
		
		//passenger start,end point
		p_latlng_from=pass_latlng_from;
		p_latlng_to=pass_latlng_to;
		//折线
		points=[
				driver_latlng_from,
				pass_latlng_from,
				pass_latlng_to,
				driver_latlng_to
				];
		onBoundsZoom(points);
		
		//创建一条折线
		polyline=new AliPolyline(points);
		//set polyline style
		polyline.setLineStyle("dashed");//solid, dotted, dashed
		//polyline.setLineWeight(5);//1 to 0
		//polyline.setOpacity(0.5);//0 to 1
		polyline.setLineColor("#ff0088");//#ff0088
		//将折线添加到地图
		map.addOverlay(polyline);


		//定义驾驶导航对象
		direction=new AliDirection();
		//设置驾驶导航结果处理函数
		AliEvent.addListener(direction,"directioncomplete",onDirectionComplete);



		//设置导航起终点
		
		direction.points=[
		          		driver_latlng_from,
		  				pass_latlng_from,
						pass_latlng_to,
						driver_latlng_to
						];
		//开始导航
		direction.execute();

		//add passenger markers
		marker_p_from=new AliMarker(pass_latlng_from,{weight:1});
		marker_p_to=new AliMarker(pass_latlng_to,{weight:1});
		marker_p_from.setIcon(icon1);
		marker_p_to.setIcon(icon2);
		map.addOverlay(marker_p_from);
		map.addOverlay(marker_p_to);
		textOverlay_p_from=new AliPointOverlay(pass_latlng_from,'<div style="color:red;font-size:15px;">乘客起点</div>',{offset:{x:0,y:-15}});
		//将该标记添加到地图
		map.addOverlay(textOverlay_p_from);
		textOverlay_p_to=new AliPointOverlay(pass_latlng_to,'<div style="color:red;font-size:15px;">乘客终点</div>',{offset:{x:0,y:-15}});
		//将该标记添加到地图
		map.addOverlay(textOverlay_p_to);
		
	}
	//处理导航结果
	function onDirectionComplete()
	{
	    var tollStr=["","全路段收费","部分路段收费"];
	    var resultDiv=document.getElementById("resultDiv"),points=[];
		//clear all child node
	    while ( resultDiv.firstChild ) resultDiv.removeChild( resultDiv.firstChild );
	    
	    //direction.steps 为导航结果路段信息     
	    for(var i=0;i<direction.steps.length;i++)
	    {
	        var step=direction.steps[i];
	        //step.getTrack()得到导航结果路段轨迹     
	        track=new AliPolyline(step.getTrack());
	        track.setLineWeight(5);//1 to 0
			//polyline.setOpacity(0.5);//0 to 1
	        //track.setLineColor("#ff0088");//#ff0088
	        
	        map.addOverlay(track);
	        var li=document.createElement("li");
	        //step.desc得到导航结果路段轨迹   
	        li.innerHTML=step.desc;
	        if(step.toll)
	        {
	            var tollDiv=document.createElement("div");
	            AliUtils.setStyle(tollDiv,{color:'#CA0000'});
	            tollDiv.appendChild(document.createTextNode(tollStr[step.toll]));
	            li.appendChild(tollDiv);
	        }
	        resultDiv.appendChild(li);
	    }
	}
	//*/
	
	//在地图被点击时执行对应的事件
	function onClick(point)
	{
		//定义一个经纬度
		g_latlng=map.fromContainerPixelToLatLng(point);
		//var str=[];
		//str.push("("+latlng.lat()+","+latlng.lng()+")");
		//alert(str.join(" "));
		g_lat=g_latlng.lat();
		g_lng=g_latlng.lng();

		
		///*
		//在该坐标处创建一个标�?
		if(marker){
			map.removeOverlay(marker);
		}
		if(marker_from) map.addOverlay(marker_from);
		if(marker_to) map.addOverlay(marker_to);
		marker=new AliMarker(g_latlng,{
		    draggable:true
		});
		//将该标记添加到地
		map.addOverlay(marker);
		
	//	AliEvent.addListener(marker,"dragstart",onDragStart);

	//	AliEvent.addListener(marker,"drag",onDrag);

		AliEvent.addListener(marker,"dragend",onDragEnd);
		//*/
		runReGeoCoding();//address
	}

	function my_onclick_Reset()
	{
		Reset();
	}
	
	function Reset()
	{
		map.clearOverlays();
		marker_from=0;
		marker_to=0;
		label_from=0;
		label_to=0;
		document.getElementById('address_from').value="";
		document.getElementById('address_to').value="";
		document.getElementById('X_from').value="";
		document.getElementById('Y_from').value="";
		document.getElementById('X_to').value="";
		document.getElementById('Y_to').value="";
	}

	

	
	function my_onclick_from()
	{
		g_lat_from=g_lat;
		g_lng_from=g_lng;
		if(g_lat_from==null)
		{
			alert("请在地图上选取地点后点击此按钮");
		}
		else
		{
		document.getElementById('X_from').value=g_lat;
		document.getElementById('Y_from').value=g_lng;

		marker_from=marker;//save marker
		label_from=label;//save label
		poiname_from=poiname;

		document.getElementById('address_from').value=poiname_from;//address
		
		//定义一个经纬度
		var latlng=new AliLatLng(g_lat_from,g_lng_from);
		//在该坐标处创建一个标记
		if(textOverlay_from) {
			map.removeOverlay(textOverlay_from);
		}
		textOverlay_from=new AliPointOverlay(latlng,'<div style="color:red;font-size:15px;">起点</div>',{offset:{x:-15,y:0}});
		//将该标记添加到地图
		map.addOverlay(textOverlay_from);
		}
	}
	function my_onclick_to()
	{
		g_lat_to=g_lat;
		g_lng_to=g_lng;
		document.getElementById('X_to').value=g_lat;
		document.getElementById('Y_to').value=g_lng;

		marker_to=marker;//save marker
		label_to=label;//save label
		poiname_to=poiname;

		document.getElementById('address_to').value=poiname_to;//address
		
		//定义一个经纬度
		var latlng=new AliLatLng(g_lat_to,g_lng_to);
		//在该坐标处创建一个标记
		if(textOverlay_to) {
			map.removeOverlay(textOverlay_to);
		}
		textOverlay_to=new AliPointOverlay(latlng,'<div style="color:red;font-size:15px;">终点</div>',{offset:{x:-15,y:0}});
		//将该标记添加到地图
		map.addOverlay(textOverlay_to);
		
	}
	function my_onclick_from_Addr()
	{
		poiname_from=document.getElementById('address_from').value;
		geocoder.getLocation(poiname_from,onGeocodingComplete_from);
		
	}
	function my_onclick_from_Addr_sub()
	{
		g_lat_from=g_lat;
		g_lng_from=g_lng;
		document.getElementById('X_from').value=g_lat;
		document.getElementById('Y_from').value=g_lng;

		
		document.getElementById('address_from').value=poiname_from;//address
		
		//定义一个经纬度
		var latlng=new AliLatLng(g_lat_from,g_lng_from);
		//在该坐标处创建一个标记
		marker_from=marker;//save marker
		map.addOverlay(marker_from);
		label_from=new AliPointOverlay(latlng,poiname_from,{style:{backgroundColor:'#0000FF',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
        map.addOverlay(label_from);
		//label_from=label;//save label
		//poiname_from=poiname;
		
		if(textOverlay_from) {
			map.removeOverlay(textOverlay_from);
		}
		textOverlay_from=new AliPointOverlay(latlng,'<div style="color:red;font-size:15px;">起点</div>',{offset:{x:-15,y:0}});
		//将该标记添加到地图
		map.addOverlay(textOverlay_from);
		//map.centerAndZoom(latlng,g_zoom);//显示地图
	}
	
	function my_onclick_to_Addr()
	{
		poiname_to=document.getElementById('address_to').value;
		geocoder.getLocation(poiname_to,onGeocodingComplete_to);
		
	}
	function my_onclick_to_Addr_sub()
	{
		g_lat_to=g_lat;
		g_lng_to=g_lng;
		document.getElementById('X_to').value=g_lat;
		document.getElementById('Y_to').value=g_lng;

		
		document.getElementById('address_to').value=poiname_to;//address
		
		//定义一个经纬度
		var latlng=new AliLatLng(g_lat_to,g_lng_to);
		//在该坐标处创建一个标记
		marker_to=marker;//save marker
		map.addOverlay(marker_to);
		label_to=new AliPointOverlay(latlng,poiname_to,{style:{backgroundColor:'#0000FF',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
        map.addOverlay(label_to);
		//label_to=label;//save label
		//poiname_to=poiname;
		
		if(textOverlay_to) {
			map.removeOverlay(textOverlay_to);
		}
		textOverlay_to=new AliPointOverlay(latlng,'<div style="color:red;font-size:15px;">起点</div>',{offset:{x:-15,y:0}});
		//将该标记添加到地图
		map.addOverlay(textOverlay_to);
		//map.centerAndZoom(latlng,g_zoom);//显示地图
		//show();
	}

	function show()
	{
		if(marker_from) map.addOverlay(marker_from);
		if(textOverlay_from) map.addOverlay(textOverlay_from);
		if(label_from) map.addOverlay(label_from);
		
		if(marker_to) map.addOverlay(marker_to);
		if(textOverlay_to) map.addOverlay(textOverlay_to);
		if(label_to) map.addOverlay(label_to);
	}
	
	function onDragStart(latlng)
	{
		var str="("+latlng.lat()+","+latlng.lng()+")";
		document.getElementById("dragStatus").innerHTML="拖动开始 "+str;
	}
	function onDrag(latlng)
	{
		var str="("+latlng.lat()+","+latlng.lng()+")";
		document.getElementById("dragStatus").innerHTML="拖动进行中…… "+str;
	}
	function onDragEnd(latlng)
	{
		//var str="("+latlng.lat()+","+latlng.lng()+")";
		//document.getElementById("dragStatus").innerHTML="拖动结束 "+str;
		g_lat=latlng.lat();
		g_lng=latlng.lng();
		runReGeoCoding();//逆地理编码
	}

	function onGeocodingComplete_from(result)
	{
	    var latlng=result.latlng;
	    g_lat=latlng.lat();
	    g_lng=latlng.lng();
	    document.getElementById("resultDiv1").innerHTML = "<b>该位置的经纬度为:</b>" + latlng.lat()+','+latlng.lng()+"<br/>"
	    +"匹配精度大约为 <b>"+result.accuracy+"</b> 米"+"<br/>"
	    +"匹配结果可信度为 <b>"+parseInt(result.exactitude*100)+"%</b>"
	    var bounds=map.getProjection().getSquare(latlng,result.accuracy);
	    oval.setBounds(bounds);
	    marker.setLatLng(latlng);
	    map.fitBounds(bounds);
	    my_onclick_from_Addr_sub();
	   
	}
	function onGeocodingComplete_to(result)
	{
	    var latlng=result.latlng;
	    g_lat=latlng.lat();
	    g_lng=latlng.lng();
	    document.getElementById("resultDiv1").innerHTML = "<b>该位置的经纬度为:</b>" + latlng.lat()+','+latlng.lng()+"<br/>"
	    +"匹配精度大约为 <b>"+result.accuracy+"</b> 米"+"<br/>"
	    +"匹配结果可信度为 <b>"+parseInt(result.exactitude*100)+"%</b>"
	    var bounds=map.getProjection().getSquare(latlng,result.accuracy);
	    oval.setBounds(bounds);
	    marker.setLatLng(latlng);
	    map.fitBounds(bounds);
	    my_onclick_to_Addr_sub();
	   
	}
	//逆地理编码
	function runReGeoCoding()
	{
	    geocoder.getAddress(marker.getLatLng(),onReGeocodingComplete);
	}
	
	function onReGeocodingComplete(result)
	{
	    var con = document.getElementById('resultDiv2'),html="";
	    //html+= "行政区划：<b>"+ result.region+"</b>(<b>"+result.regionCode+"</b>)<br/>";
	    for(var key in tags)
	    {
	        map.removeOverlay(tags[key]);
	        tags[key].depose();
	    }
	    if(result.nearestRoad)
	    {
	        html+= "附近道路：<b>"+ result.nearestRoad.name+"</b>(<b>"+result.nearestRoad.distance+"</b>米)<br/>";
	        //tags.road=new AliPointOverlay(result.nearestRoad.latlng,result.nearestRoad.name,{style:{backgroundColor:'#FF0000',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
	        //map.addOverlay(tags.road);
	    }
	    if(result.nearestPoi)
	    {
	        html+= "附近POI：<b>"+ result.nearestPoi.name+"</b>(<b>"+result.nearestPoi.distance+"</b>米)<br/>";
	        //tags.poi=new AliPointOverlay(result.nearestPoi.latlng,result.nearestPoi.name,{style:{backgroundColor:'#00FF00',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
	        tags.poi=new AliPointOverlay(result.nearestPoi.latlng,result.nearestPoi.name,{style:{backgroundColor:'#0000FF',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
	        map.addOverlay(tags.poi);
	    }
	    if(result.nearestDoorPlate)
	    {
	        html+= "附近门址：<b>"+ result.nearestDoorPlate.name+"</b>(<b>"+result.nearestDoorPlate.distance+"</b>米)<br/>";
	        //tags.door=new AliPointOverlay(result.nearestDoorPlate.latlng,result.nearestDoorPlate.name,{style:{backgroundColor:'#0000FF',border:'solid 1px #FFFFFF',color:'#FFFFFF',fontSize:'12px',whiteSpace:'noWrap'}});
	       // map.addOverlay(tags.door);
	 
	    }
	    con.innerHTML =html;
	    label=tags.poi;
		poiname=result.nearestPoi.name;
		
	    if(label_from) map.addOverlay(label_from);
	    if(label_to) map.addOverlay(label_to);
	}

	function match_gettrace()
	{
		map.clearOverlays();
		map.addOverlay(marker_from);
		//map.addOverlay(textOverlay_from);
		//map.addOverlay(label_from);
		
		map.addOverlay(marker_to);
		//map.addOverlay(textOverlay_to);
		//map.addOverlay(label_to);
		
		
		points=[
				new AliLatLng(g_lat_from,g_lng_from),
				new AliLatLng(g_lat_to,g_lng_to)
				];
		onBoundsZoom(points);
		
		//创建一条折线
		polyline=new AliPolyline(points);
		//set polyline style
		polyline.setLineStyle("dashed");//solid, dotted, dashed
		//polyline.setLineWeight(5);//1 to 0
		//polyline.setOpacity(0.5);//0 to 1
		polyline.setLineColor("#ff0088");//#ff0088
		//将折线添加到地图
		map.addOverlay(polyline);


		//定义驾驶导航对象
		direction=new AliDirection();
		//设置驾驶导航结果处理函数
		AliEvent.addListener(direction,"directioncomplete",onDirectionComplete);
		//设置导航起终点
		direction.points=[new AliLatLng(g_lat_from,g_lng_from),
		  				new AliLatLng(g_lat_to,g_lng_to)];
		//开始导航
		direction.execute();
		
	}

	//when click in passenger's name and change it to another passenger, update all information
	function onChangeOption()
	{

		var selIndex = document.form1.Name.selectedIndex;
		//selIndex=2;
		//document.form1.Cellphone.value=selIndex;
		if(selIndex!=-1){

			//alert(selIndex);
			document.form1.Cellphone.value=pArray[selIndex][2];
			document.form1.start_time.value=pArray[selIndex][5];
			document.form1.end_time.value=pArray[selIndex][6];
			document.form1.Angle.value=pArray[selIndex][13];

			document.form1.address_from.value=pArray[selIndex][7];
			document.form1.X_from.value=pArray[selIndex][8];
			document.form1.Y_from.value=pArray[selIndex][9];
			
			document.form1.address_to.value=pArray[selIndex][10];
			document.form1.X_to.value=pArray[selIndex][11];
			document.form1.Y_to.value=pArray[selIndex][12];

			//document.form1.Sex.value=pArray[selIndex][14];
			
			///*
			g_pass_lat_from=document.form1.X_from.value;
			g_pass_lng_from=document.form1.Y_from.value;
			g_pass_address_from=document.form1.address_from.value;
			
			g_pass_lat_to=document.form1.X_to.value;
			g_pass_lng_to=document.form1.Y_to.value;
			g_pass_address_to=document.form1.address_to.value;

			g_pass_angle=document.form1.Angle.value;
			//*/

			//the first time it is not working
			trace_map();
		}
		else
		{
			alert("selIndex=-1; error of option selection");
		}
		//trace_map();
	}

	function wait(msecs)
	{
		var start = new Date().getTime();
		var cur = start
		while(cur - start < msecs)
		{
			cur = new Date().getTime();
		}	
	} 
	
	function onShowFirstOption()
	{

		var selIndex = 0;
		//document.form1.Cellphone.value=selIndex;
		
		if(selIndex!=-1){

			//alert(selIndex);
			document.form1.Cellphone.value=pArray[selIndex][2];
			document.form1.start_time.value=pArray[selIndex][5];
			document.form1.end_time.value=pArray[selIndex][6];
			document.form1.Angle.value=pArray[selIndex][13];

			document.form1.address_from.value=pArray[selIndex][7];
			document.form1.X_from.value=pArray[selIndex][8];
			document.form1.Y_from.value=pArray[selIndex][9];
			
			document.form1.address_to.value=pArray[selIndex][10];
			document.form1.X_to.value=pArray[selIndex][11];
			document.form1.Y_to.value=pArray[selIndex][12];

			//document.form1.Sex.value=pArray[selIndex][14];
			
			///*
			g_pass_lat_from=document.form1.X_from.value;
			g_pass_lng_from=document.form1.Y_from.value;
			g_pass_address_from=document.form1.address_from.value;
			
			g_pass_lat_to=document.form1.X_to.value;
			g_pass_lng_to=document.form1.Y_to.value;
			g_pass_address_to=document.form1.address_to.value;

			g_pass_angle=document.form1.Angle.value;
			//*/
			//alert(g_pass_lat_from);
			//wait(1000);
			//the first time it is not working
			trace_map();
		}
		else
		{
			alert("selIndex=-1; error of option selection");
		}
		//trace_map();
	}

	function onPnumberChange()
	{
		alert("OnPnumberChange");
		
	}
	//add the patient names
	function onAddOptions()
	{
		document.form1.Name.options.length=0;
		document.form1.Name.options[0]=new Option(pArray[0][1],pArray[0][1],true,true);
		//document.form1.Name.selectedIndex=1;
		///*
		for (i=1;i<pArray.length;i++)
		{
			document.form1.Name.options[i]=new Option(pArray[i][1],pArray[i][1],false,false);
			
		}
		//*/
	}
	
	Jla.onReady(onLoad);
	
</script>

<style type="text/css">
<!--
.style6 {color: #F0F0F0}
.style7 {color: #00FF00}
.style8 {color: #CC0000}
-->
</style>
</head>

<body onload="onload()">

  <?php //load _post data from driver page
	$Name=$_POST['Name'];
	$Cellphone=$_POST['Cellphone'];
	$Lat_from=$_POST['X_from'];
	$Lng_from=$_POST['Y_from'];
	$Lat_to=$_POST['X_to'];
	$Lng_to=$_POST['Y_to'];
	$Address_from=$_POST['address_from'];
	$Address_to=$_POST['address_to'];
	$Start_time=$_POST['start_time'];
	$End_time=$_POST['end_time'];
	$Angle=$_POST['Angle'];
	$Sex=$_POST['Sex'];
	$Charge=$_POST['Charge'];
	$SpecialReq=$_POST['SpecialReq'];
	$PassNumber=$_POST['PassNumber'];
	$PackNumber=$_POST['PackNumber'];
	//echo "address from is $Address_from";
	//echo "$Address_to";
	//echo "Lat from is $Lat_from";
?>
<script type="text/javascript">
//get all driver's information

	g_driver_lat_from=<?php echo $Lat_from;?>;
	g_driver_lng_from=<?php echo $Lng_from;?>;
	g_driver_lat_to=<?php echo $Lat_to;?>;
	g_driver_lng_to=<?php echo $Lng_to;?>;
	
	g_driver_angle=<?php echo $Angle;?>;

	g_driver_address_from="<?php echo $Address_from;?>";
	g_driver_address_to="<?php echo $Address_to;?>";
	
	g_driver_end_time="<?php $End_time;?>";
	g_driver_start_time="<?php echo $Start_time;?>";
	g_driver_name="<?php echo $Name;?>";
	g_driver_cellphone="<?php echo $Cellphone;?>";
	//*/
	//alert(g_driver_lat_from);
	
</script>

<?php //database operation
	
	//connect the server
	$con=mysql_connect("localhost","root","9945015");
	//$con=mysql_connect("Carpoolandroid.db.12690699.hostedresource.com","Carpoolandroid","Carpool2014!");
	if(!$con)		{		die('Could not connect: '.mysql_error());	}
	//connect database
	mysql_select_db("CarpoolDatabase");
	//mysql_select_db("baby51ping");
	mysql_query("SET NAMES 'utf8'");
	
	//if the passenger name is not NULL, then add this as a record of passenger
	/*
	if($Name<>NULL and $Lat_from<>NULL and $Lat_to<>NULL and $Cellphone<>NULL){
		
		$ID=date("YmdGis");
		echo "id is $ID";
	 	$query = "insert into Carpool_Driver 
	 		(ID, Name, Lat_from, Lng_from,Lat_to,Lng_to,Address_from,Address_to,Angle,Start_time,End_time) 
	 		values ('$ID','$Name','$Lat_from','$Lng_from','$Lat_to','$Lng_to','$Address_from','$Address_to','$Angle','$Start_time','$End_time')";
	 	
		
		$result = mysql_query($query);
	    if($result)
	    {
	   	 $id=mysql_insert_id();
	   	 echo "\n生成唯一用户ID是： $ID";
	    }
	    else{
	    	echo "\nerror when adding information";
	    }
	}
	//*/
	//get the passenger's information from table Carpool_Passenger in the database
	    $query = "select * from carpool_driver order by ID";
		$result = mysql_query($query);
		if(!$result){
	    	    	echo "\nerror when loading information";
	    }
		
    ?>
    
<form action="" method="post" name="form1" target="_blank" id="form1" >
  <p>满足您的要求乘客如下:</p>
  <p>乘客<span class="style5">姓名</span>
    <select name="Name" id="Name" onchange="onChangeOption()">
      <option selected value="a1">a1</option>
      <option value="a2">a2</option>
    </select>  
    <span class="style5">性别</span>
    <select name="Sex" id="Sex">
        <option>先生</option>
        <option>女士</option>
    </select>
    <span class="style5">手机#
    <input name="Cellphone" type="text" id="Cellphone" value="" size="13">
乘客数目
<select name="pnumber" size="1" onchange="onPnumberChange()">
    <option value="1">1</option>
    <option value="2">2</option>
    <option value="3">3</option>
</select>
大包裹数目</span>
    <select name="select2" size="1">
        <option>1</option>
        <option>2</option>
        <option>3</option>
    </select>
    <input type="submit" name="Submit" value="提交拼车申请" >
  </p>
  <p><span class="style5">出发时间
      <input name="start_time" type="text" id="start_time" value="" size="15"> 
      <img src="images/cal.gif" onclick="javascript:NewCssCal ('start_time','yyyyMMdd','dropdown',true,'24','','future')" style="cursor:pointer"/>
      
    到
    <input name="end_time" type="text" id="end_time" value="" size="15"> 
    <img src="images/cal.gif" onclick="javascript:NewCssCal ('end_time','yyyyMMdd','dropdown',true,'24','','future')" style="cursor:pointer"/>
    
    最多接受金额    
    <input name="textarea2" type="text" value="" size="3">
元     特殊要求
<input name="textarea8" type="text" value="" size="16">
角度
<input name="Angle" type="text" id="Angle" value="" size="6">
  </span>
    <input name="Get_trace" type="button" id="Get_trace2" value="显示路径" onClick="my_onclick_gettrace()">
<label></label>
  </p>
  <p>
  <label></label>
  <input name="Set_From" type="button" id="Set_From" value="乘客出发地点" onClick="my_onclick_from()">  
  <span class="style5">X  
  <input name="X_from" type="text" id="X_from" value="">
  Y  
  <input name="Y_from" type="text" id="Y_from" value="">
  地址
  </span>
  <input name="address_from" type="text" id="address_from" value="" size="45">
  <input name="Set_From_Addr" type="button" id="Set_From_Addr" value="输入出发" onClick="my_onclick_from_Addr()">
  </p>
  <p>    <label>  </label>
    <input name="Set_To" type="button" id="Set_To" value="乘客目的地点" onClick="my_onclick_to()">    
    <span class="style5">X
    <input name="X_to" type="text" id="X_to" value="">
    Y 
    <input name="Y_to" type="text" id="Y_to" value="">
    地址    </span>
    <input name="address_to" type="text" id="address_to" value="" size="45"> 
    <input name="Set_To_Addr" type="button" id="Set_To_Addr" value="输入目的" onClick="my_onclick_to_Addr()">
</p>
  
  
  <table width="900" height="500" border="1">
    <tr>
      <td bordercolor="#0000FF" bgcolor="#FFFFFF">
      	<div id="mapDiv" style="width:900px;height:500px"></div>
      </td>
    </tr>
  </table>
  
</form>

<script type="text/javascript">
//get all record into pArray
<?php 
if($result)
{	
	$j=0;
	while($row = mysql_fetch_array( $result ))
	{ 
		?>
		<?php for($i=0;$i<sizeof($row)/2;$i++){			?>
		
		valueToPush[<?php echo $i;?>]="<?php echo $row[$i];?>";
		<?php 
		}		?>
		pArray[<?php echo $j?>]=new Array();
		pArray[<?php echo $j?>]=valueToPush.slice();
		document.write(valueToPush);
		//alert('<?php echo "alert"; ?>');
		
		document.write("j is "+"<?php echo $j;?>"+"<br>");
	<?php //loop
	$j++;
	} 
}?>

//document.write(pArray); //show all the stuff in the pArray
onAddOptions();
//alert(g_pass_lat_from);
onShowFirstOption();


//document.form1.Cellphone.value=1999;
//alert("abc");

</script>	
<br> 导航路径为: <br>
<ol id="resultDiv"></ol>
</body>

</html>