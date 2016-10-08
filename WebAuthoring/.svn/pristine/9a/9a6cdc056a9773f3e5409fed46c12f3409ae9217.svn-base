/**
*
*/
function buildGrids(gridPixelSize, color, gap, div)
{
	var canvas = $('#'+div+'').get(0);
	var ctx = canvas.getContext("2d");
	 
	ctx.lineWidth = 1;
	ctx.strokeStyle = color;
	 
	 
	// horizontal grid lines
	for(var i = 0; i <= canvas.height; i = i + gridPixelSize)
	{
		ctx.beginPath();
		ctx.moveTo(0, i);
		ctx.lineTo(canvas.width, i);
		if(i % parseInt(gap, 10) === 0) 
		{
			ctx.lineWidth = 2;
		} 
		else 
		{
			ctx.lineWidth = 0.5;
		}
		
		ctx.closePath();
		ctx.stroke();
	}
	 
	// vertical grid lines
	for(var j = 0; j <= canvas.width; j = j + gridPixelSize)
	{
		ctx.beginPath();
		ctx.moveTo(j, 0);
		ctx.lineTo(j, canvas.height);
		
		if(j % parseInt(gap, 10) === 0) 
		{
			ctx.lineWidth = 2;
		} 
		else 
		{
			ctx.lineWidth = 0.5;
		}
		
		ctx.closePath();
		ctx.stroke();
	}
}
