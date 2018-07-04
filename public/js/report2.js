/**
 * Created by qianpeng on 2017/6/20.
 */

var printContent = function() {
    window.print();
}


$(function(){
    var data = JSON.stringify({
        "token": $.cookie("token"),
        "reportid": md5report
    })
    ajaxData("/data/query/reportparameter", data, "POST", function(r){
        if(r.status == "ok") {
            if (r.parameter.condition.product_name != undefined){
                $(".categoryclasstitle").text(" • " + r.parameter.condition.product_name);
                $(".categoryclass").text(r.parameter.condition.product_name);
                $(".biaoti").text(r.parameter.condition.product_name)
            }else if (r.parameter.condition.oral_name != undefined){
                $(".categoryclasstitle").text(" • " + r.parameter.condition.oral_name);
                $(".categoryclass").text(r.parameter.condition.oral_name);
                $(".biaoti").text(r.parameter.condition.oral_name)
            }else {
                $(".categoryclasstitle").text(" • " + r.parameter.condition.category);
                $(".categoryclass").text(r.parameter.condition.category);
                $(".biaoti").text(r.parameter.condition.category)
            }
            reportajax($.extend(r.parameter, {"token": $.cookie("token")}))
        }
    }, function(e){console.error(e)})

    // $("#xiaojieatcname").text("嘿嘿嘿")
});

var reportajax = function(data) {
    ajaxData("/data/calc/report/summary", JSON.stringify(data), "POST", function(r2){
        reportgraphone(r2.reportgraphone)
        reportgraphfour(r2.reportgraphfour)
        reportgraphfive(r2.reportgraphfive)
        reportgraphseven(r2.reportgraphseven)
        reportgrapheight(r2.reportgrapheight)
        reportgraphsix(r2.reportgraphsix)
        // reportTableOne(r2.ReportTableOne)
        if (r2 != undefined){
            $("#loading").fadeOut("slow");
        }
    }, function(e2){console.error(e2)})
}
var reportTableOne=function (obj) {
    // var productNumber=obj.productNumber.toArray().map()
    // var manufacture_type=obj.manufacture_type
    // var percentGrowth=obj.percentGrowth
    // var marketShare=obj.marketShare
    // window.console.log(productNumber)
}
var reportgraphone = function(obj) {
    var xAxisData = [], seriesData_sales = [], seriesData_trend = [];
    $.each(obj, function (i, v) {
        xAxisData.push(v.start + "-" + v.end);
        seriesData_sales.push(v.sales);
        seriesData_trend.push((v.trend * 100).toFixed(2));
    });
    option = {
        title: {text: "销售额表现", left: 'center', top:'4%'},
        tooltip: {trigger: 'axis'},
        legend: {data:['销售额','增长率']},
        xAxis: [{type: 'category', data: xAxisData}],
        yAxis: [
            {type: 'value', name: '销售额', axisLabel: {formatter: '{value} ￥'}},
            {type: 'value', name: '增长率', axisLabel: {formatter: '{value} %'}}
        ],
        series: [
            {name:'销售额', type:'bar', data:seriesData_sales, barWidth: "15%"},
            {name:'增长率', type:'line', yAxisIndex: 1, data:seriesData_trend}
            ]
    };

    var Histogram = Echart("reportgraphone", "vintage")
    Histogram.setOption(option);

    var marketstarttime = xAxisData[xAxisData.length-2]
    var marketendtime = xAxisData[xAxisData.length-1]
    var markettrehd = seriesData_trend[seriesData_trend.length-1]
    var markesales = seriesData_sales[seriesData_sales.length-1]
    $(".hospmarketsummarytime").text(marketstarttime+"至"+marketendtime)
    $(".hospmarketsummarytrend").text(markettrehd+"%")
    $(".hospmarketsummaryendtime").text(marketendtime)
    $(".hospmarketsummarysales").text((markesales/100000000).toFixed(2))
}

var reportgrapheight = function (obj) {
    var legend = [];
    var yAxis = [];
    var series = [];
    $.each(obj, function(i, v){
        $.each(v.keyvalue, function(n, k) {
            $.each(k, function(j, l) {
                legend.push(j)
            })
        })
        yAxis.push(v.start + "-" + v.end)
    })
    legend = Array.from(new Set(legend))
    var d = [];
    $.each(legend, function(i, v) {
        d = []
        $.each(yAxis, function(n, k) {
            $.each(obj, function(j, l) {
                if(k == (l.start + "-" + l.end)) {
                    $.each(l.keyvalue, function(o, p) {
                        $.each(p, function(b,m) {
                            if(v == b) {
                                d.push((m * 100).toFixed(2))
                            }
                        })
                    })
                }
            })
        })
        series.push({
            name : v,
            type : "bar",
            data : d
        })
    })
    var option = {
        title: {text: "医院市场Top10品牌销售额占比", left: 'center', top:'4%'},
        tooltip: {trigger: 'axis', axisPointer: {type: 'shadow'},formatter: function(params) {
            var c = params[0].axisValue
            $.each(params, function(i, v){
                c = c + '<br/>' + v.marker + v.seriesName + "：" + v.value+'%'
            })
           return c
        }},
        legend: {
            data: legend
        },
        grid: {left: '3%', right: '4%', bottom: '3%', containLabel: true},
        xAxis: {
            type: 'value',
            boundaryGap: [0, 0.01]
        },
        yAxis: {
            type: 'category',
            data: yAxis
        },
        series: series
    };
    var Histogram = Echart("reportgrapheight", "vintage")
    Histogram.setOption(option);
    var inter = 0;
    var outer = 0;
    var internametemp = [];
    var intername = "";
    var lst = [];
    var top = "";
    var sales = [];
    var internamesum = 0;
    $.each(obj, function(i, v){
        if((v.start + "-" + v.end) == yAxis[0]) {
            $.each(v.interouter, function(j, k){
                $.each(k, function(o, p){
                    if(p == "合资") outer++
                    else {
                        internametemp.push(o)
                        inter++
                    }
                })
            })
            $.each(v.keyvalue, function(i, k){
                $.each(k, function(o, p){
                    lst.push({key: o, value: p})
                    $.each(internametemp, function(n, c){
                        if(o == c){
                            internamesum = internamesum + p
                            intername = intername + o +"、"
                        }
                    })
                })
            })
            top = lst.sort(function(a, b){return a.value < b.value ? 1 : -1})
            $.each(v.manufacture, function(i, k){
                if(top[0].key == k.product_name) {
                    $(".top10manufacture").text(k.manufacture)
                    $(".top10manufactureproduct").text(top[0].key)
                    $(".top10manufactureproducttrend").text((top[0].value * 100).toFixed(2) + "%")
                }
            })
            sales = v.sales
        }
    })
    var temp = yAxis.reverse()
    var productstarttime = temp[temp.length-2]
    var productendtime = temp[temp.length-1]
    var top3name = ""
    var top3sales = ""
    $(".top10producttime").text(productstarttime + "至" + productendtime)
    $("#top10productendtime").text(productendtime)
    $("#top10productouter").text(outer)
    $(".top10productinter").text(inter)
    $(".topone").text(top[0].key)
    $.each(sales.sort(function(a,b){return b.sales - a.sales}), function(i, v){
        if(i<=2) {
            top3name = top3name + v.product_name + "、"
            top3sales = top3sales + v.sales + "、"
        }
    })

    $(".producttime").text(productendtime)
    $(".producttop3").text(top3name)
    $(".producttop3salse").text(top3sales)
    $(".top10productname").text(intername)
    $(".top10productnametrendsum").text((internamesum * 100).toFixed(2)+"%")
    $(".productendtime").text(productendtime)

}

var reportgraphseven = function(obj) {
    var xAxisData = [], seriesData_sales = [], seriesData_trend = [];
    $.each(obj, function (i, v) {
        xAxisData.push(v.start + "-" + v.end);
        seriesData_sales.push((v.sales * 100).toFixed(2));
        seriesData_trend.push((v.trend * 100).toFixed(2));
    });
    option = {
        tooltip: {trigger: 'axis'},
        legend: {data:['销售额占比','占比增长率']},
        xAxis: [{type: 'category', data: xAxisData}],
        yAxis: [
            {type: 'value', name: '销售额占比', axisLabel: {formatter: '{value} %'}},
            {type: 'value', name: '占比增长率', axisLabel: {formatter: '{value} %'}}
        ],
        series: [
            {name:'销售额占比', type:'bar', data:seriesData_sales, barWidth: "15%"},
            {name:'占比增长率', type:'line', yAxisIndex: 1, data:seriesData_trend}
        ]
    };

    var Histogram = Echart("reportgraphseven", "vintage")
    Histogram.setOption(option);
    $(".top10producttrend").text(seriesData_sales.sort()[0]+"%")
}

var reportgraphfive = function(obj) {
    var legend = [];
    var xAxis = [];
    var series = [];

    $.each(obj, function (i, v) {
        $.each(v.sales, function (n, k) {
            legend.push(k.key)
        })
        xAxis.push(v.start + "-" + v.end)
    })
    legend = Array.from(new Set(legend))
    xAxis = xAxis.reverse()

    var bar = [];
    var line = [];
    var trend = [];
    $.each(legend, function(i, v) {
        bar = []
        line = []
        $.each(xAxis, function(n, k) {
            $.each(obj, function(j, l) {
                if(k == (l.start + "-" + l.end)) {
                    $.each(l.sales, function(o, p) {
                        if(v == p.key) {
                            bar.push(p.value)
                        }
                    })
                    $.each(l.scale, function(b,m) {
                        if(v == m.key) {
                            line.push((m.value * 100).toFixed(2))
                        }
                    })
                }
            })
        })
        trend.push(line[line.length-1])
        series.push({
            name: v,
            type: 'bar',
            stack: '总量',
            label: {normal: {show: true, position: 'inside'}},
            data: bar,
            barWidth: "15%"
        },{
            name: v,
            type: 'line',
            yAxisIndex: 1,
            data: line
        })
    })
    var option = {
        tooltip : {trigger: 'axis', axisPointer : {type : 'shadow'}},
        legend: {data: legend},
        grid: {left: '3%', right: '4%', bottom: '3%', containLabel: true},
        xAxis:  {type: 'category', data: xAxis},
        yAxis: [
            {type: 'value', name: '销售额', axisLabel: {formatter: '{value} ￥'}},
            {type: 'value', name: '占有率', axisLabel: {formatter: '{value} %'}}
        ],
        series: series
    };

    var Histogram = Echart("reportgraphfive", "vintage")
    Histogram.setOption(option);
    var categorystarttime = xAxis[xAxis.length-2]
    var categoryendtime = xAxis[xAxis.length-1]
    var lst = ""
    var lsttrend = ""
    $.each(legend, function(i, v){
        lst = lst + v + "、"
        lsttrend = lsttrend + trend[i] + "%" + "、"
    })
    var top3lst = []
    $.each(obj, function(i, v){
        if((v.start + "-" + v.end) == xAxis[xAxis.length-1]) {
            top3lst = v.scale.sort(function(a,b){return b.value - a.value}).slice(0,3)
        }
    })
    var oralendtime = legend[legend.length-1]
    $(".oraltime").text(categorystarttime + "至" + categoryendtime)
    $("#categorylst").text(lst)
    $("#categorylsttrend").text(lsttrend)
    $(".oralendtime").text(oralendtime)
    var top3oral = ""
    var top3trend = ""
    $.each(top3lst, function (i, v) {
        top3oral = top3oral + v.key + "、"
        top3trend = top3trend + (v.value * 100).toFixed(2) + "%" + "、"
    })
    $(".top3oraltrend").text(top3trend)
    $(".top3oral").text(top3oral)

}

var reportgraphfour = function(obj) {
    var xAxis = [], inter=[], outer = [], percent = [];

    $.each(obj, function(i, v){
        xAxis.push(v.start + "-" + v.end)
        inter.push(v.inter)
        outer.push(v.outer)
        percent.push(parseFloat(v.percent).toFixed(2))
    });
    xAxis = xAxis.reverse()
    inter = inter.reverse()
    outer = outer.reverse()
    percent = percent.reverse()

    var option = {
        title: {text: "合资与内资企业销售表现", left: 'center', top:'4%'},
        tooltip: {trigger: 'axis', axisPointer: {type: 'cross', crossStyle: {color: '#999'}}},
        legend: {
            data:['合资','内资','占比']
        },
        xAxis: [
            {type: 'category', data: xAxis, axisPointer: {type: 'shadow'}}
        ],
        yAxis: [
            {type: 'value', name: '销售额', axisLabel: {formatter: '{value} ￥'}},
            {type: 'value', name: '占比', axisLabel: {formatter: '{value} %'}}
        ],
        series: [
            {name: '合资', type: 'bar', data: outer, barWidth: "15%"},
            {name: '内资', type: 'bar', data: inter, barWidth: "15%"},
            {name: '占比', type: 'line', yAxisIndex: 1, data: percent}
        ]
    };
    var Histogram = Echart("reportgraphfour", "vintage")
    Histogram.setOption(option);

    var enterprisestarttime = xAxis[xAxis.length-2]
    var enterpriseendtime = xAxis[xAxis.length-1]
    var enterprisepercent = percent[percent.length-1]
    var enterprisesalses = outer[outer.length-1]
    var enterpriseintersales = inter[inter.length-1]

    $(".enterprisetime").text(enterprisestarttime + "至" + enterpriseendtime)
    $(".enterprisepercent").text(enterprisepercent+"%")
    $(".enterpriseendtime").text(enterpriseendtime)
    $(".enterprisesales").text((enterprisesalses / 100000000).toFixed(2))
    $(".enterpriseintersales").text((enterpriseintersales / 100000000).toFixed(2))


}

var reportgraphsix = function(obj) {
    var legend = [];
    var xAxis = [];
    var series = [];
    $.each(obj, function (i, v) {
        $.each(v.keyvalue, function (n, k) {
            $.each(k, function(j, l){
                xAxis.push(j)
            })
        })
        legend.push(v.start + "-" + v.end)
    })
    xAxis = Array.from(new Set(xAxis))

    var d = [];

    $.each(legend, function(i, v) {
        d = []
        $.each(xAxis, function (n, k) {
            $.each(obj, function(j, l) {
                if(v == (l.start + "-" + l.end)){
                    $.each(l.keyvalue, function(o, p){
                        $.each(p, function(b,m) {
                            if(k == b) {
                                d.push(m)
                            }
                        })
                    })
                }
            })
        })
        series.push({
            name : v,
            type : "bar",
            data : d,
            barWidth: "15%"
        })
    })

    var option = {
        tooltip : {
            trigger: 'axis',
            axisPointer: {type: 'shadow', label: {show: true}}
        },
        calculable : true,
        legend: {data: legend, itemGap: 5},
        grid: {top: '12%', left: '1%', right: '10%', containLabel: true},
        xAxis: [{type : 'category', data : xAxis}],
        yAxis: [{type : 'value', name : '销售额￥',
                axisLabel: {
                    formatter: function (a) {
                        a = +a;
                        return isFinite(a)
                            ? echarts.format.addCommas(+a / 1000)
                            : '';
                    }
                }
            }
        ],
        dataZoom: [{show: true, start: 94, end: 100}, {type: 'inside', start: 94, end: 100},
                   {show: true, yAxisIndex: 0, filterMode: 'empty', width: 30, height: '80%', showDataShadow: false, left: '93%'}
        ],
        series : series
    };
    var Histogram = Echart("reportgraphsix", "vintage")
    Histogram.setOption(option);
}