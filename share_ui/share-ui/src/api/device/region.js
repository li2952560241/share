import request from '@/utils/request'

export function getTreeSelect(parentCode) {
  return request({
    url: '/device/region/treeSelect/' + parentCode,
    method: 'get'
  })
}
