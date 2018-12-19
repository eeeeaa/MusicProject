from pymongo import MongoClient
from flask import Flask
from flask import request
from flask import jsonify

app = Flask(__name__)
@app.route("/")
def ping():
    client = MongoClient('mongodb://tigermaster:aass12rr@ds145997.mlab.com:45997/myfavorite')
    return 'server online'

@app.route("/addVideo")
def addVideo():
    client = MongoClient('mongodb://tigermaster:aass12rr@ds145997.mlab.com:45997/myfavorite')
    db=client.myfavorite
    video=db.video
    videoId = request.args.get('videoId')
    videoName=request.args.get('videoName')
    URLthumbnail=request.args.get('URLthumbnail')
    video.insert_one({'videoName':videoName,'URLthumbnail':URLthumbnail,'videoId' : videoId})
    return 'added'

@app.route("/findVideo")
def findVideo():
    client = MongoClient('mongodb://tigermaster:aass12rr@ds145997.mlab.com:45997/myfavorite')
    db=client.myfavorite
    video=db.video
    videoId = str(request.args.get('videoId'))
    results = video.find_one({"videoId" : videoId})
    if results is not None:
        return jsonify({'videoId': results['videoId'],'videoName':results['videoName'],'URLthumbnail':results['URLthumbnail']})
    else:
        return jsonify({'videoId': None,'videoName': None,'URLthumbnail': None})

  

@app.route("/getallVideo")
def getallVideo():
  client = MongoClient('mongodb://tigermaster:aass12rr@ds145997.mlab.com:45997/myfavorite')
  db=client.myfavorite
  video=db.video
  results=video.find()
  ret=[]
  for item in results:
      ret.append({'videoId': item['videoId'],'videoName':item['videoName'],'URLthumbnail':item['URLthumbnail']})
  return jsonify(ret)

@app.route("/removeVideo")
def removeVideo():
  client = MongoClient('mongodb://tigermaster:aass12rr@ds145997.mlab.com:45997/myfavorite')
  db=client.myfavorite
  video=db.video
  videoId = request.args.get('videoId')
  video.delete_one({'videoId':videoId})
  return 'removed'
  
if __name__ == '__main__':
    app.run(debug=True, use_reloader=True)
