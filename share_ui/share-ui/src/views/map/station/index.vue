<template>
  <div class="app-container">
    <div style="padding: 0 0 20px 0;">
      <el-input v-model="keyword" placeholder="请输入关键字" style="width: 92%;margin-right: 10px;"/>
      <el-button type="primary" @click="search()" style="width: 5%;">搜索</el-button>
    </div>
    <div id="container"></div>

  </div>
</template>

<script setup name="Station">
import { calculateLatLng } from "@/api/device/map";
import { nearbyStation } from "@/api/device/station";
const { proxy } = getCurrentInstance();

let map;
let markerLayer;
const keyword = ref("");

function search() {
  if(keyword.value == '') return;
  calculateLatLng(keyword.value).then(response => {
    let data = response.data;
    let position = new TMap.LatLng(data.lat, data.lng)
    map.setCenter(position);

    //渲染地图数据
    initMapData(position);
  });

}

let infoWindowList = Array(100);
function initMap() {
  let center = new TMap.LatLng(39.984104, 116.307503);
  //初始化地图
  map = new TMap.Map(document.getElementById('container'), {
    rotation: 20,//设置地图旋转角度
    pitch:30, //设置俯仰角度（0~45）
    zoom:12,//设置地图缩放级别
    center: center//设置地图中心点坐标
  });

  //初始化marker图层
  markerLayer = new TMap.MultiMarker({
    id: 'marker-layer',
    map: map
  });

  //显示中心点
  // markerLayer.add({
  //      position: center
  // })

  initMapData(center);

  //监听地图中心点变化
  map.addListener('center_changed', function() {
    console.log("latlng:" + map.getCenter());
  });
}

function initMapData(center) {
  markerLayer.setGeometries([])
  nearbyStation(center.lat, center.lng).then(response => {
    let latLngList = response.data;
    for (let i = 0; i < latLngList.length; i++) {
      let station = latLngList[i];
      let position = new TMap.LatLng(station.latitude, station.longitude)

      // 参考示例 https://lbs.qq.com/webDemoCenter/glAPI/glServiceLib/search
      let geometries = markerLayer.getGeometries();
      geometries.push({
        id: String(i), // 点标注数据数组
        position: position
      });
      markerLayer.updateGeometries(geometries); // 绘制地点标注
      // markerLayer.add({
      //   position: position
      // })

      let isUsable = "不可借";
      if(station.isUsable == '1') {
        isUsable = "可借";
      }
      let isReturn = "不可还";
      if(station.isReturn == '1') {
        isReturn = "可还";
      }
      //创建InfoWindow实例，并进行初始化
      var infoWindow = new TMap.InfoWindow({
        map: map,
        position: position,
        offset: { x: 0, y: -32 },
        //设置infoWindow，content支持直接传入html代码，以实现各类内容格式需求
        content: "<div class=\"info-container\">\n" +
            "  <div class=\"image-container\">\n" +
            "    <img src=\""+station.imageUrl+"\">\n" +
            "  </div>\n" +
            "  <div class=\"text-container\">\n" +
            "   <p style='font-weight: bold;'>"+station.name+"</p>\n" +
            "   <p style='font-size: 12px;'>"+station.fullAddress+"</p>\n" +
            "   <p>运营时间："+station.businessHours+"</p>\n" +
            "   <p>"+isUsable+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+isReturn+"</p>\n" +
            "  </div>\n" +
            "</div>"
      });
      infoWindowList[i] = infoWindow;
      infoWindow.close();//初始关闭信息窗关闭
      //监听标注点击事件
      markerLayer.on("click", function (evt) {
        //关闭全部信息窗
        infoWindowList.forEach(item => item.close());
        // 打开点击的信息窗
        infoWindowList[Number(evt.geometry.id)].open();
      })
    }
  });
}

onMounted(() => {
  initMap();
})
</script>
<style scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>
<style>

.info-container {
  display: flex;
  align-items: flex-start; /* 垂直居中对齐 */
  gap: 20px; /* 图片和文字之间的间距 */
}

.image-container {
  flex: 1; /* 图片区域占一半的空间 */
}

.image-container img {
  width: 120px; /* 图片自适应容器大小 */
  height: 120px; /* 保持图片的宽高比 */
}

.text-container {
  flex: 2; /* 文字区域占三分之二的空间 */
  width: 210px;
  margin-top: -10px;
}
.text-container p{
  text-align: left;
  margin: 10px 0;
}
</style>
