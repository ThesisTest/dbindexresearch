var insertUrl = '/research2/insert';

layui.use(['layer', 'element'], function () {
    var layer = layui.layer;
    var element = layui.element;
    var $ = layui.$;

    //数据插入表初始化
    var insertChart = echarts.init(document.getElementById("divLeftTop"));
    var insertOption = {
        title: {
            text: '数据插入时间表'
        },
        color: ['#003366', '#006699', '#4cabce'],
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            data: ['LinearHash', 'ExtensibleHash', 'BPlusTree']
        },
        calculable: true,
        xAxis: [
            {
                type: 'category',
                axisTick: {show: false},
                name: '索引字段',
                data: ['图书名(数据稀疏)', '图书系列(数据密集)']
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '时间(单位ns)'
            }
        ],
        series: [
            {
                name: 'LinearHash',
                type: 'bar',
                barGap: 0,
                data: [0, 0]
            },
            {
                name: 'ExtensibleHash',
                type: 'bar',
                barGap: 0,
                data: [0, 0]
            },
            {
                name: 'BPlusTree',
                type: 'bar',
                barGap: 0,
                data: [0, 0]
            }]
    };
    insertChart.setOption(insertOption);

    $("#insert").click(function () {
        $.post(insertUrl, {num: 100}, function (res) {
            if (res.code === 0){
                insertOption.series[0].data[0] += res.data.linearHash1Time;
                insertOption.series[0].data[1] += res.data.linearHash2Time;
                insertOption.series[1].data[0] += res.data.extensibleHash1Time;
                insertOption.series[1].data[1] += res.data.extensibleHash2Time;
                insertOption.series[2].data[0] += res.data.bPlusTree1Time;
                insertOption.series[2].data[1] += res.data.bPlusTree1Time;
                insertChart.setOption(insertOption);
            }
        });
    });
});