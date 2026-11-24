import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { LogicTaskAttachVO, LogicTaskAttachForm, LogicTaskAttachQuery } from '@/api/doclogic/logicTaskAttach/types';

/**
 * 查询逻辑任务附件列表
 * @param query
 * @returns {*}
 */

export const listLogicTaskAttach = (query?: LogicTaskAttachQuery): AxiosPromise<LogicTaskAttachVO[]> => {
  return request({
    url: '/doclogic/logicTaskAttach/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询逻辑任务附件详细
 * @param id
 */
export const getLogicTaskAttach = (id: string | number): AxiosPromise<LogicTaskAttachVO> => {
  return request({
    url: '/doclogic/logicTaskAttach/' + id,
    method: 'get'
  });
};

/**
 * 新增逻辑任务附件
 * @param data
 */
export const addLogicTaskAttach = (data: LogicTaskAttachForm) => {
  return request({
    url: '/doclogic/logicTaskAttach',
    method: 'post',
    data: data
  });
};

/**
 * 修改逻辑任务附件
 * @param data
 */
export const updateLogicTaskAttach = (data: LogicTaskAttachForm) => {
  return request({
    url: '/doclogic/logicTaskAttach',
    method: 'put',
    data: data
  });
};

/**
 * 删除逻辑任务附件
 * @param id
 */
export const delLogicTaskAttach = (id: string | number | Array<string | number>) => {
  return request({
    url: '/doclogic/logicTaskAttach/' + id,
    method: 'delete'
  });
};
