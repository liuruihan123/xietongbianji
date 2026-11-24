import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { CryptoMonitoringLogVO, CryptoMonitoringLogForm, CryptoMonitoringLogQuery } from '@/api/bicanmou/CryptoMonitoringLog/types';

/**
 * 查询监控日志列表
 * @param query
 * @returns {*}
 */

export const listCryptoMonitoringLog = (query?: CryptoMonitoringLogQuery): AxiosPromise<CryptoMonitoringLogVO[]> => {
  return request({
    url: '/bicanmou/CryptoMonitoringLog/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询监控日志详细
 * @param id
 */
export const getCryptoMonitoringLog = (id: string | number): AxiosPromise<CryptoMonitoringLogVO> => {
  return request({
    url: '/bicanmou/CryptoMonitoringLog/' + id,
    method: 'get'
  });
};

/**
 * 新增监控日志
 * @param data
 */
export const addCryptoMonitoringLog = (data: CryptoMonitoringLogForm) => {
  return request({
    url: '/bicanmou/CryptoMonitoringLog',
    method: 'post',
    data: data
  });
};

/**
 * 修改监控日志
 * @param data
 */
export const updateCryptoMonitoringLog = (data: CryptoMonitoringLogForm) => {
  return request({
    url: '/bicanmou/CryptoMonitoringLog',
    method: 'put',
    data: data
  });
};

/**
 * 删除监控日志
 * @param id
 */
export const delCryptoMonitoringLog = (id: string | number | Array<string | number>) => {
  return request({
    url: '/bicanmou/CryptoMonitoringLog/' + id,
    method: 'delete'
  });
};
