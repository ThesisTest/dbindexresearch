var initUrl = "/research3/init";
var equalSearchUrl = "/research3/equalSearch";
var scopeSearchUrl = "/research3/scopeSearch";

layui.use(['code', 'element', 'form'], function () {
    var element = layui.element;
    var form = layui.form;
    var $ = layui.$;
    layui.code({
        title:'查询结果',
        about:false,
        skin:'notepad'
    });

    $.post(initUrl, {num:100000}, function (res) {
        console.log(res.msg);
    });

    $("#equalSearch").click(function () {
        var price = $("#price").val();
        if (price === ""){
            alert("请输入价格！");
            return;
        }
        $.post(equalSearchUrl, {bookPrice: price}, function (res) {
            var content = '';
            content += 'LinearHash: ' + res.data.linearHashResults + '\n';
            content += 'ExtensibleHash: ' + res.data.extensibleHashResults + '\n';
            content += 'B+Tree: ' + res.data.bPlusTreeResults + '\n';
            $("#equalSearchDiv").text(content);
        });
    });

    $("#scopeSearch").click(function () {
        var priceFrom = $("#priceFrom").val();
        var priceTo = $("#priceTo").val();
        if (priceFrom === ""){
            alert("请输入最低价格！");
            return;
        }
        if (priceTo === ""){
            alert("请输入最高价格！");
            return;
        }
        $.post(scopeSearchUrl, {bookPrice1: priceFrom, bookPrice2: priceTo}, function (res) {
            var content = '';
            content += 'LinearHash: ' + res.data.linearHashResults + '\n';
            content += 'ExtensibleHash: ' + res.data.extensibleHashResults + '\n';
            content += 'B+Tree: ' + res.data.bPlusTreeResults + '\n';
            $("#scopeSearchDiv").text(content);
        });
    });

});