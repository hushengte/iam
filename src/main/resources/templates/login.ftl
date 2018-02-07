<#import "/spring.ftl" as spring/>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="charset=utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    
    <title>登录</title>
    
    <link rel="stylesheet" href="<@spring.url '/css/bootstrap.min.css'/>"/>
</head>
<body style="background-color: #e4e5e6;">
    <div class="col-md-10" style="margin-top: 130px;">
		<div class="row">
	        <div class="col-md-4 col-md-offset-5">
	            <div class="panel panel-default">
	                <div class="panel-heading">
	                    <h3 class="panel-title">请登录</h3>
	                </div>
	                <div class="panel-body">
	       <#if error??><div class="col-md-offset-5">
	                		<label class="text-danger">帐号或者密码错误</label>
	                	</div></#if>
	                    <form class="form-horizontal" role="form" name="loginForm" method="post" action="<@spring.url '/login.do'/>">
	                    	<div class="form-group">
						        <label for="username" class="col-md-3 control-label text-success">帐号：</label>
						        <div class="col-md-9">
						            <input type="text" class="form-control" name="username" value="<#if username??>${username}</#if>"/>
						        </div>
						    </div>
						    <div class="form-group">
						        <label for="password" class="col-md-3 control-label text-success">密码：</label>
						        <div class="col-md-9">
						            <input type="password" class="form-control" name="password"/>
						        </div>
						    </div>
						    <button type="submit" class="btn btn-lg btn-success btn-block">登录</button>
	                    </form>
	                </div>
	            </div>
	        </div>
	    </div>
	</div>
</body>
</html>
