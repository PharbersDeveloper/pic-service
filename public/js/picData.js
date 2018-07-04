/**
 * Created by yym on 8/1/17.
 */
$("document").ready(function () {
    intialRawData()
});
function intialRawData() {
    RawData(1)


}
function RawData(nextIndex){
    var token= $.cookie("token");
    var d=JSON.stringify({
        "token" : token,
        "nextIndex" : nextIndex
    })
    $.ajax({
        type:"POST",
        url:"/rawData/read",
        data:d,
        dataType:"json",
        contentType: "application/json,charset=utf-8",
        success:function(res){
            console.log(res)
            if(res.status=="ok"){
                var t=$(".rawData").find("tbody");
                $.each(res.result,function(i,item){
                    t.append("<tr>" +
                        "<td>" +item.GenericName+"</td>"+
                        "<td>" +item.CompanyName+"</td>"+
                        "<td>" +item.Year+"</td>"+
                        "<td>" +item.SalesAmount+"</td>"+
                        "<td>" +item.Quantity+"</td>"+
                        "<td>" +item.Specification+"</td>"+
                        "<td>" +item.Formulation+"</td>"+
                        "<td>" +item.Quarter+"</td>"+
                        "<td>" +item.SinglePackage+"</td>"+
                        "<td>" +item.ROA+"</td>"+
                        "<td>" +item.TherapyMicro+"</td>"+
                        "<td>" +item.TherapyWide+"</td>"+
                        "<td>" +item.City+"</td>"+
                        "</tr>")
                });
                pageIntial(res.pages)
            }
        }
    })
};
function getRawData(obj,nextIndex){
    var token= $.cookie("token");
    var d=JSON.stringify({
        "token" : token,
        "nextIndex" : nextIndex
    })
    $.ajax({
        type:"POST",
        url:"/rawData/read",
        data:d,
        dataType:"json",
        contentType: "application/json,charset=utf-8",
        success:function(res){
           if(res.status=="ok"){
                obj.addClass("active")
               var t=$(".rawData").find("tbody");
               $.each(res.result,function(i,item){
                   t.append("<tr>" +
                       "<td>" +item.GenericName+"</td>"+
                       "<td>" +item.CompanyName+"</td>"+
                       "<td>" +item.Year+"</td>"+
                       "<td>" +item.SalesAmount+"</td>"+
                       "<td>" +item.Quantity+"</td>"+
                       "<td>" +item.Specification+"</td>"+
                       "<td>" +item.Formulation+"</td>"+
                       "<td>" +item.Quarter+"</td>"+
                       "<td>" +item.SinglePackage+"</td>"+
                       "<td>" +item.ROA+"</td>"+
                       "<td>" +item.TherapyMicro+"</td>"+
                       "<td>" +item.TherapyWide+"</td>"+
                       "<td>" +item.City+"</td>"+
                       "</tr>")
               });
               return res.pages;
           }else{
               return -1;
           }
        }
    })
};
function pageIntial(pageTotal) {
        console.log(pageTotal)
    for(var i=pageTotal;i>=1;i--){
        $("#pre").after("<li><a href='#' onclick='getRawData($(this),"+i+")'>"+i+"</a></li>")
    }
}