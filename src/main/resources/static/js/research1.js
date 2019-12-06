var insertUrl = '/research1/insert';
var searchUrl = '/research1/search';
var deleteUrl = '/research1/delete';

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
                name: '时间(单位ns)'
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
                name: '时间(单位ns)'
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
                name: '时间(单位ns)'
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
        $.post(insertUrl, {num: 10}, function (res) {
            if (res.code === 0){
                insertOption.series[0].data.push(res.data.linearHashTime);
                insertOption.series[1].data.push(res.data.extensibleHashTime);
                insertOption.series[2].data.push(res.data.bPlusTreeTime);
                insertChart.setOption(insertOption);
                $("#insert1000").attr("disabled", "disabled");
            }
        });
    });

    //插入1w条数据
    $("#insert1w").click(function () {
        $.post(insertUrl, {num: 100}, function (res) {
            if (res.code === 0){
                insertOption.series[0].data.push(res.data.linearHashTime);
                insertOption.series[1].data.push(res.data.extensibleHashTime);
                insertOption.series[2].data.push(res.data.bPlusTreeTime);
                insertChart.setOption(insertOption);
                $("#insert1w").attr("disabled", "disabled");
            }
        });
    });

    //插入10w条数据
    $("#insert10w").click(function () {
        $.post(insertUrl, {num: 1000}, function (res) {
            if (res.code === 0){
                insertOption.series[0].data.push(res.data.linearHashTime);
                insertOption.series[1].data.push(res.data.extensibleHashTime);
                insertOption.series[2].data.push(res.data.bPlusTreeTime);
                insertChart.setOption(insertOption);
                $("#insert10w").attr("disabled", "disabled");
            }
        });
    });

    //插入100w条数据
    $("#insert100w").click(function () {
        $.post(insertUrl, {num: 10000}, function (res) {
            if (res.code === 0){
                insertOption.series[0].data.push(res.data.linearHashTime);
                insertOption.series[1].data.push(res.data.extensibleHashTime);
                insertOption.series[2].data.push(res.data.bPlusTreeTime);
                insertChart.setOption(insertOption);
                $("#insert100w").attr("disabled", "disabled");
            }
        });
    });

    //插入1000w条数据
    $("#insert1000w").click(function () {
        $.post(insertUrl, {num: 100000}, function (res) {
            if (res.code === 0){
                insertOption.series[0].data.push(res.data.linearHashTime);
                insertOption.series[1].data.push(res.data.extensibleHashTime);
                insertOption.series[2].data.push(res.data.bPlusTreeTime);
                insertChart.setOption(insertOption);
                $("#insert1000w").attr("disabled", "disabled");
            }
        });
    });

    $("#search").click(function () {
        $.post(searchUrl, {bookName:'图书1000'}, function (res) {
            if (res.code === 0){
                searchOption.series[0].data.push(res.data.linearHashTime);
                searchOption.series[1].data.push(res.data.extensibleHashTime);
                searchOption.series[2].data.push(res.data.bPlusTreeTime);
                searchChart.setOption(searchOption);
                if (res.data.linearHashResults.length === 0){
                    res.data.linearHashResults.push(1001);
                }
                if (res.data.extensibleHashResults.length === 0){
                    res.data.extensibleHashResults.push(1001);
                }
                if (res.data.bPlusTreeResults.length === 0){
                    res.data.bPlusTreeResults.push(1001);
                }
                console.log("linear hash:" + res.data.linearHashResults);
                console.log("extensible hash:" + res.data.extensibleHashResults);
                console.log("B+ tree index:" + res.data.bPlusTreeResults);
            }
        });
    });

    $("#delete1").click(function () {
        $.post(deleteUrl, {num: 1}, function (res) {
            if (res.code === 0){
                deleteOption.series[0].data.push(res.data.linearHashTime);
                deleteOption.series[1].data.push(res.data.extensibleHashTime);
                deleteOption.series[2].data.push(res.data.bPlusTreeTime);
                deleteChart.setOption(deleteOption);
                $("#delete1").attr("disabled", "disabled");
            }
        });
    });

    $("#delete10").click(function () {
        $.post(deleteUrl, {num: 10}, function (res) {
            if (res.code === 0){
                deleteOption.series[0].data.push(res.data.linearHashTime);
                deleteOption.series[1].data.push(res.data.extensibleHashTime);
                deleteOption.series[2].data.push(res.data.bPlusTreeTime);
                deleteChart.setOption(deleteOption);
                $("#delete10").attr("disabled", "disabled");
            }
        });
    });

    $("#delete100").click(function () {
        $.post(deleteUrl, {num: 100}, function (res) {
            if (res.code === 0){
                deleteOption.series[0].data.push(res.data.linearHashTime);
                deleteOption.series[1].data.push(res.data.extensibleHashTime);
                deleteOption.series[2].data.push(res.data.bPlusTreeTime);
                deleteChart.setOption(deleteOption);
                $("#delete100").attr("disabled", "disabled");
            }
        });
    });

    $("#delete1000").click(function () {
        $.post(deleteUrl, {num: 1000}, function (res) {
            if (res.code === 0){
                deleteOption.series[0].data.push(res.data.linearHashTime);
                deleteOption.series[1].data.push(res.data.extensibleHashTime);
                deleteOption.series[2].data.push(res.data.bPlusTreeTime);
                deleteChart.setOption(deleteOption);
                $("#delete1000").attr("disabled", "disabled");
            }
        });
    });

    $("#delete1w").click(function () {
        $.post(deleteUrl, {num: 10000}, function (res) {
            if (res.code === 0){
                deleteOption.series[0].data.push(res.data.linearHashTime);
                deleteOption.series[1].data.push(res.data.extensibleHashTime);
                deleteOption.series[2].data.push(res.data.bPlusTreeTime);
                deleteChart.setOption(deleteOption);
                $("#delete1w").attr("disabled", "disabled");
            }
        });
    });
});