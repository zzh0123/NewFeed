package ys.app.feed.constant;

public class Constants {

    public static final String AppID = "wx83733416a7bd9097"; // 微信appid  2c348a64704a79f5f866097eb58c2c11   AppSecret
    public static final String AppSecret = "2c348a64704a79f5f866097eb58c2c11"; // 微信appid  2c348a64704a79f5f866097eb58c2c11

//    public static final String baseUrl = "http://192.168.3.77:8080/pasture-app/";  //测试ip
    public static final String baseUrl = "http://www.huihemuchang.com/pasture-app/"; // 正式ip

    public static final String start_img_url = "http://www.huihemuchang.com/nfs/advertisement/advertisement.png";  // 开机广告
    public static final String start_video_url = "http://www.huihemuchang.com/nfs/advertisement/huihe.mp4";  // 了解慧河视频
    public static final String start_video_url1 = "http://www.huihemuchang.com/nfs/advertisement/huihe1.mp4";  // 了解慧河视频
    public static final String start_video_url2 = "http://www.huihemuchang.com/nfs/advertisement/huihe2.mp4";  // 了解慧河视频
    public static final String start_video_url3 = "http://www.huihemuchang.com/nfs/advertisement/huihe3.mp4";  // 了解慧河视频
    public static final String start_video_url4 = "http://www.huihemuchang.com/nfs/advertisement/huihe4.mp4";  // 了解慧河视频

    public static final String url_apk = "http://www.huihemuchang.com/nfs/apk/app-release.apk";  // apk下载地址

    // 分享微信群url
    public static final String url_shareLuckDraw = "http://www.huihemuchang.com/pasture-app/shareLuckDraw";  // 抽奖券
    public static final String url_shareLuckDrawCash = "http://www.huihemuchang.com/pasture-app/shareLuckDrawCash";  // 代金券
    public static final String url_shareLuckDrawBargain = "http://www.huihemuchang.com/pasture-app/shareLuckDrawBargain";  // 砍价券

    public static final String url_shareLuckDrawSpell = "http://www.huihemuchang.com/pasture-app/shareLuckDrawSpell";  // 免拼卡
    public static final String url_shareLuckDrawHalf = "http://www.huihemuchang.com/pasture-app/shareLuckDrawHalf";  // 半价券
    public static final String url_welfareIndex = "http://www.huihemuchang.com/pasture-app/welfareIndex";  // 下载app


//    public static final String url_share_order = "http://192.168.3.77:8080/pasture-app/shareOrder";  // 订单分享
    public static final String url_share_order = "http://www.huihemuchang.com/pasture-app/shareOrder";  // 正式订单分享

//    public static final String url_groupInvite= "http://192.168.3.77:8080/pasture-app/groupInvite";  // 团购订单邀请
    public static final String url_groupInvite = "http://www.huihemuchang.com/pasture-app/groupInvite";  // 正式团购订单分享

//    public static final String url_share_order_helpCutInvite = "http://192.168.3.77:8080/pasture-app/helpCutInvite";  // 砍价订单分享
    public static final String url_share_order_helpCutInvite = "http://www.huihemuchang.com/pasture-app/helpCutInvite";  // 正式砍价订单分享

//    public static final String url_accurate_batching = "http://192.168.3.77:8080/pasture-app/accurate";  // 精准配料
    public static final String url_accurate_batching = "http://www.huihemuchang.com/pasture-app/accurate";  // 正式精准配料

//    public static final String url_discount = "http://192.168.3.77:8080/pasture-app/discount";  // 实惠导购
        public static final String url_discount = "http://www.huihemuchang.com/pasture-app/discount";  // 正式实惠导购

    // common
    public static final String url_inspectionUpdate = "common/inspectionUpdate";  // 检验更新

    public static final String url_get_verification_code = "sms/getCode";  // 获取验证码
    public static final String url_forgetPassword = "login/forgetPassword";  // 忘记密码
    public static final String url_login = "login/login";  // 密码、验证码登录

    public static final String url_wxLogin = "login/wxLogin";  // 微信登录

    // user
    public static final String url_getGiftPackage = "user/getGiftPackage";  // 获取新人礼包
    public static final String url_getUserScoreData = "user/getUserScoreData";  // 获取用户积分

    public static final String url_getUserInfo = "user/getUserInfo";  // 获取用户信息
    public static final String url_getUserInfoDetail = "user/getUserInfoDetail";  // 获取用户详细信息

    public static final String url_signIn = "user/signIn";  // 签到

    public static final String url_changeUserInfo = "user/changeUserInfo";  // 编辑用户信息

    public static final String url_getInviteMode = "user/getInviteMode";  // 获取邀请方式
    public static final String url_getInviteData = "user/getInviteData";  // 获取我的邀请列表

    public static final String url_addUserIntention = "user/addUserIntention";  // 添加用户意向产品

    public static final String url_luckDraw = "user/luckDraw";  // 积分红包


    // address
    public static final String url_addReceivingAddress = "address/addReceivingAddress";  // 添加地址
    public static final String url_getReceivingAddressData = "address/getReceivingAddressData";  // 获取地址
    public static final String url_deleteReceivingAddress = "address/deleteReceivingAddress";  // 删除地址
    public static final String url_changeDefaultAddress = "address/changeDefaultAddress";  // 修改默认地址

    // 支付
    public static final String url_alipay = "pay/alipay";  // 支付宝支付
    public static final String url_wxpay = "pay/wxpay";  // 微信支付

    // 商城 mall
    public static final String url_getUnreadMessageCount = "mall/getUnreadMessageCount";  // 消息个数
    public static final String url_getMessageData = "mall/getMessageData";  // 消息列表
    public static final String url_getMessageDetail = "mall/getMessageDetail";  // 消息详情
    public static final String url_getBannerData = "mall/getBannerData";  // 获取轮播图片

    public static final String url_globalSearch = "mall/globalSearch";  // 全局搜索

    public static final String url_buyLuckCoupon = "mall/buyLuckCoupon";  // 购买抽奖券

    public static final String url_getLuckDrawData = "mall/getLuckDrawData";  // 获取抽奖物品

    public static final String url_getBuyScoreData = "mall/getBuyScoreData";  // 购买积分列表

    public static final String url_createBuyScoreOrder = "mall/createBuyScoreOrder";  // 创建购买积分订单

    public static final String url_reduceLuckCoupon = "mall/reduceLuckCoupon";  // 扣除用户抽奖券

    public static final String url_mall_luckDraw = "mall/luckDraw";  // 抽奖


    // commodity
    public static final String url_getCommodityData = "commodity/getCommodityData";  // 获取全部商品列表
    public static final String url_getCommodityTitle = "commodity/getCommodityTitle";  //  获取商品标题
    public static final String url_getGroupData = "commodity/getGroupData";  // 获取团列表


    //    public static final String url_getHalfCommodityData = "mall/getHalfCommodityData";  //  获取半价商品列表
//    public static final String url_getCommodityHalfDetail = "commodity/getCommodityHalfDetail";  //  获取半价商品详情

    //    public static final String url_getScoreCommodityData = "mall/getScoreCommodityData";  //  获取积分兑换商品列表
//    public static final String url_getCommodityScoreDetail = "commodity/getCommodityScoreDetail";  //  获取积分兑换商品详情

    public static final String url_getCommodityDetail = "commodity/getCommodityDetail";  // 获取商品详情

    public static final String url_getMoreRecommend = "commodity/getMoreRecommend";  // 获取用户更多推荐

    public static final String url_publishComment = "commodity/publishComment";  // 发表商品评论
    public static final String url_getCommentData = "commodity/getCommentData";  // 获取商品评论列表

    // 会员
    public static final String url_getMemberType = "member/getMemberType";  // 获取会员类型,金额
    public static final String url_isBuyMember = "member/isBuyMember";  // 判断用户是否可以购买体验会员
    public static final String url_addMemberOrder = "member/addMemberOrder";  // 创建会员订单

    // 优惠券
    public static final String url_getCouponByTyper = "coupon/getCouponByTyper";  // 按照类型获取用户未使用的优惠券
    public static final String url_getCouponData = "coupon/getCouponData";  // 获取用户优惠券列表
    public static final String url_getCouponByType = "coupon/getCouponByType";  // 按照类型获取用户未使用的优惠券


    // order
    public static final String url_getGroupScale = "order/getGroupScale";  // 获取团规模
    public static final String url_createGroup = "order/createGroup";  // 创建团
    public static final String url_createOrderGroup = "order/createOrderGroup";  // 创建订单-团购
    public static final String url_getScoreDeductionData = "order/getScoreDeductionData";  // 积分抵扣接口调用

    public static final String url_createOrderHalf = "order/createOrderHalf";  // 创建订单-半价
    public static final String url_createOrderScore = "order/createOrderScore";  // 创建订单-积分
    public static final String url_createOrderCut = "order/createOrderCut";  // 创建订单-砍价

    public static final String url_createOrder = "order/createOrder";  // 创建订单-单独

    public static final String url_deleteOrder = "order/deleteOrder";  // 删除订单

    public static final String url_getOrderData = "order/getOrderData";  // 获取订单列表

    public static final String url_getOrderHalfDetail = "order/getOrderHalfDetail";  // 获取订单详情-半价
    public static final String url_getOrderDetail = "order/getOrderDetail";  // 获取订单详情-普通
    public static final String url_getOrderGroupDetail = "order/getOrderGroupDetail";  // 获取订单详情-团购
    public static final String url_getOrderScoreDetail = "order/getOrderScoreDetail";  // 获取订单详情-积分

    public static final String url_getOrderCutDetail = "order/getOrderCutDetail";  // 获取订单详情-砍价

    public static final String url_goHelpCut = "order/goHelpCut";  // 砍价

    public static final String url_shareOrderAddScore= "order/shareOrderAddScore";  // 订单分享加积分

    public static final String url_changeOrderStatus= "order/changeOrderStatus";  // 签收

    // collection
    public static final String url_commodityCollection = "collection/commodityCollection";  // 商品收藏和取消收藏
    public static final String url_getCollectionData = "collection/getCollectionData";  // 获取用户收藏商品

    // extension
    public static final String url_getExtensionData = "extension/getExtensionData";  // 获取推广中心数据
    public static final String url_launchCashCash = "extension/launchCashCash";  // 申请提现
}
