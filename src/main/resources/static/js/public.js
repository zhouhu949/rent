var tokenId = $.cookie('rent_tokenId');

var header = {}; //或者 var obj=new Object();
header["tokenId"] = tokenId;

var requestBaseUrl = getRootPath();




//js获取项目根路径，如： http://localhost:8083/rent
function getRootPath(){
    //获取当前网址，如： http://localhost:8083/rent/admin/news.action
    var curWwwPath=window.document.location.href;
    //获取主机地址之后的目录，如： rent/admin/news.action
    var pathName=window.document.location.pathname;
    var pos=curWwwPath.indexOf(pathName);
    //获取主机地址，如： http://localhost:8083
    var localhostPaht=curWwwPath.substring(0,pos);
    //获取带"/"的项目名，如：/rent
    var projectName=pathName.substring(0,pathName.substr(1).indexOf('/')+1);
    return(localhostPaht+projectName);
}