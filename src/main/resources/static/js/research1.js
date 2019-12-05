var insertUrl = '/research1/';

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
                name: '数据量',
                data: ['1000', '1w', '10w', '100w', '1000w']
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '时间(单位ms)'
            }
        ],
        series: [
            {
                name: 'LinearHash',
                type: 'bar',
                barGap: 0,
                data: []
            },
            {
                name: 'ExtensibleHash',
                type: 'bar',
                barGap: 0,
                data: []
            },
            {
                name: 'BPlusTree',
                type: 'bar',
                barGap: 0,
                data: []
            }]
    };
    insertChart.setOption(insertOption);

    //数据查询表初始化
    var searchChart = echarts.init(document.getElementById("divLeftBottom"));
    var searchOption = {
        title: {
            text: '数据查询时间表'
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
                name: '数据量',
                data: ['1000', '1w', '10w', '100w', '1000w']
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '时间(单位ms)'
            }
        ],
        series: [
            {
                name: 'LinearHash',
                type: 'bar',
                barGap: 0,
                data: []
            },
            {
                name: 'ExtensibleHash',
                type: 'bar',
                barGap: 0,
                data: []
            },
            {
                name: 'BPlusTree',
                type: 'bar',
                barGap: 0,
                data: []
            }]
    };
    searchChart.setOption(searchOption);

    //数据删除表初始化
    var deleteChart = echarts.init(document.getElementById("divRightBottom"));
    var deleteOption = {
        title: {
            text: '数据删除时间表'
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
                name: '数据量',
                data: ['1', '10', '100', '1000', '1w']
            }
        ],
        yAxis: [
            {
                type: 'value',
                name: '时间(单位ms)'
            }
        ],
        series: [
            {
                name: 'LinearHash',
                type: 'bar',
                barGap: 0,
                data: []
            },
            {
                name: 'ExtensibleHash',
                type: 'bar',
                barGap: 0,
                data: []
            },
            {
                name: 'BPlusTree',
                type: 'bar',
                barGap: 0,
                data: []
            }]
    };
    deleteChart.setOption(deleteOption);

    //插入1000条数据
    $("#insert1000").click(function () {

    });
});