const AWS = require('aws-sdk');
const axios = require('axios')
const s3 = new AWS.S3();

const endpoint = process.env.ENDPOINT || 'http://host.docker.internal:8080'

/**
 * A Lambda function that logs the payload received from S3.
 */
exports.s3FileToStreamClient = async (event, context) => {
  // All log statements are written to CloudWatch by default. For more information, see
  // https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-logging.html
  const getObjectRequests = event.Records.map(record => {
    const params = {
      Bucket: record.s3.bucket.name,
      Key: record.s3.object.key
    };
    return callApi(params)
  });
  return Promise.all(getObjectRequests).then(() => {
    console.debug('Complete!');
  });
};

async function callApi(params){
  let data = await s3.getObject(params).promise()
  data = JSON.parse(data.Body.toString())
  console.log("data is ", data)
  const result = await axios.post(`${endpoint}/test`, data);
  console.log("api response ", result)
}
