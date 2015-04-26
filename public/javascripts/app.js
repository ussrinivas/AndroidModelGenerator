var count = 1;
var modelCount = 0;
// var first = new object();
// first.number = 1;
// first.fields = new Array();
var models_list = [ 1 ];

function tempModel() {
	this.name = "Sample Framework";
}

var modelsList = new Array();
function Field() {
	this.name = "";
	this.fieldType = "";
}

function Model() {
	this.name = "";
	this.idName = "table_";
	this.number = 0;
	this.fieldIndex = 0;
	this.fieldsList = new Array();
}

function Database() {
	this.name = "";
	this.packageName = "";
	this.models = modelsList
}

model0 = new Model();
model0.number = 0;

modelsList[0] = model0;

var database = new Database();

function addPackge(input) {
	database.packageName = $('#' + input.name).val();
}

function addDatabase(input) {
	database.name = $('#' + input.name).val();
}

function addModels() {
	$('#models_div')
			.append(
					'<div id="div_'
							+ count
							+ '">'
							+ '<input type="text" id=table_'
							+ count
							+ ' name=table_'
							+ count
							+ ' class="input-large" placeholder="Model name" onkeyup="modelContent(this, '
							+ count + ')" />'
							+ '  <a href="#" onclick="deleteModel(' + count
							+ ')"><i class="icon-remove"></i></a><br /></div>')

	model = new Model();
	model.number = count;
	model.idName = "table_" + count;
	modelsList[count] = model;

	models_list.push(count)
	count += 1
	// alert(models_list)
}

function getModel(modelNumber) {
	var currentModel;
	for ( var i = 0; i < modelsList.length; i++) {
		if (modelsList[i] && (modelsList[i].number == modelNumber)) {
			currentModel = modelsList[i];
			break;
		}
	}
	return currentModel;
}

function deleteModel(modelNumber) {
	for ( var i = 0; i < modelsList.length; i++) {
		if (modelsList[i] && (modelsList[i].number == modelNumber)) {
			alert(modelsList[i].number);
			$('#div_' + modelsList[i].number).remove();
			$('#li_model_table_' + modelsList[i].number).remove()
			$('#model_table_' + modelsList[i].number).remove()
			modelsList.splice(i, 1);
			count--;
			break;
		}
	}
}

function saveFieldInfo(modelNumber, fieldNumber) {
	var currentModel = getModel(modelNumber);
	content = $('#' + 'table_' + modelNumber + '_field_' + fieldNumber).val();

	currentModel.fieldsList[fieldNumber].name = content;
}

function generateJson() {
	// alert(JSON.stringify(database));
	$('#code_tabs').empty();
	$('#code_content_tabs').empty();
	$.ajax({
		type : "POST",
		url : "/models",
		async : true,
		data : JSON.stringify(database),
		contentType : "application/json",
		complete : function(content) {
			var code = JSON.parse(content.responseText);
			for (var i = 0; i < code.results.length; i++) {
			    $('#code_tabs').append('<li id=li_code_content_' + i + '><a data-toggle="tab" href=#code_content_' + i + '>' + code.results[i].name + '</a></li>');
			    $('#code_content_tabs').append(
			    	'<div class="tab-pane" id=code_content_'+ i +'> <pre class="syntax scala">' + code.results[i].code + ' </pre> </div>');
			}
			$('#li_code_content_0').addClass('active');
			$('#code_content_0').addClass('active');
			// $('#code_content_0').addClass('class = "active"');
			// $('#result').html(code.results[0].code);
		}
	});
}

function deleteField(modelNumber, fieldNumber) {
	// body...
}

function saveFieldTypeInfo(modelNumber, fieldNumber) {
	var currentModel = getModel(modelNumber);
	content = $('#' + 'table_' + modelNumber + '_field_type_' + fieldNumber)
			.val();

	currentModel.fieldsList[fieldNumber].fieldType = content;
}

function addFields(div_tab_id, modelNumber) {
	var currentModel = getModel(modelNumber);

	$('#' + div_tab_id)
			.append(
					'<div id=model_'
							+ name
							+ '_field>'
							+ '<input type="text" placeholder="Field name" id=table_'
							+ modelNumber
							+ '_field_'
							+ currentModel.fieldIndex
							+ ' onkeyup="saveFieldInfo('
							+ modelNumber
							+ ', '
							+ currentModel.fieldIndex
							+ ')">'
							+ '<select class="selectpicker" style="margin-left: 5px; margin-right: 5px;" id=table_'
							+ modelNumber
							+ '_field_type_'
							+ currentModel.fieldIndex
							+ ' onchange="saveFieldTypeInfo('
							+ modelNumber
							+ ', '
							+ currentModel.fieldIndex
							+ ')">  <option>String</option>   <option>Int</option>   <option>Long</option> </select>'
							+ '<a href="#" onclick="deleteField(' + modelNumber
							+ ', ' + currentModel.fieldIndex
							+ ')"><i class="icon-remove"></i></a></div>');
	currentModel.fieldsList[currentModel.fieldIndex] = new Field();
	currentModel.fieldsList[currentModel.fieldIndex].fieldType = "String";
	currentModel.fieldIndex += 1;

}

// '<input type="text" id="database_' + $(this).val() + '" name="database_' +
// $(this).val() + '" class="input-large" placeholder="Field Name">' +

function modelContent(input, modelNumber) {
	name = input.name;
	content = $('#' + name).val();
	var model_ = $('#model_' + name);
	var ref_ = $('#ref_model_' + name);
	var currentModel = getModel(modelNumber);

	if (model_.length != 0 && content.length == 0) {
		$('#ref_model_' + name).remove();
		$('#model_' + name).remove();
	} else if (model_.length != 0) {
		text = model_.val()
		currentModel.name = content;
		// model_.text(content);
		ref_.text(content);
	} else {
		if (content != "") {
			$('#user_tabs').append(
					'<li id=li_model_' + name + '><a href="#model_' + name
							+ '" id=ref_model_' + name + ' data-toggle="tab">'
							+ content + '</a></li>');
			$('#model_tabs')
					.append(
							'<div class="tab-pane" id=model_'
									+ name
									+ '>'
									+ '<button type="submit" class="btn" style="margin: 4px;" onclick="addFields(\'model_'
									+ name
									+ '\', '
									+ modelNumber
									+ ')">Add field</button>'
									+ '<div id=model_'
									+ name
									+ '_field>'
									+ '<input type="text" placeholder="Field name" id=table_'
									+ modelNumber
									+ '_field_0 onkeyup="saveFieldInfo('
									+ modelNumber
									+ ', '
									+ 0
									+ ')">'
									+ '<select class="selectpicker" style="margin-left: 5px; margin-right: 5px;" id=table_'
									+ modelNumber
									+ '_field_type_'
									+ 0
									+ ' onchange="saveFieldTypeInfo('
									+ modelNumber
									+ ', '
									+ 0
									+ ')">  <option>String</option>   <option>Int</option>   <option>Long</option> </select>'
									+ '<a href="#" onclick="deleteField('
									+ modelNumber
									+ ', '
									+ 0
									+ ')"><i class="icon-remove"></i></a></div>'
									+ '</div>' + '</div>');
			currentModel.name = content;
			currentModel.fieldsList[currentModel.fieldIndex] = new Field();
			currentModel.fieldsList[currentModel.fieldIndex].fieldType = "String";
			currentModel.fieldIndex += 1;
		}
	}
}