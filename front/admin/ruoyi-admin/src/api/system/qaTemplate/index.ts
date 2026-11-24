import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { QaTemplateVO, QaTemplateForm, QaTemplateQuery } from '@/api/system/qaTemplate/types';

/**
 * 查询问答模版列表
 * @param query
 * @returns {*}
 */

export const listQaTemplate = (query?: QaTemplateQuery): AxiosPromise<QaTemplateVO[]> => {
  return request({
    url: '/system/qaTemplate/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询问答模版详细
 * @param id
 */
export const getQaTemplate = (id: string | number): AxiosPromise<QaTemplateVO> => {
  return request({
    url: '/system/qaTemplate/' + id,
    method: 'get'
  });
};

/**
 * 新增问答模版
 * @param data
 */
export const addQaTemplate = (data: QaTemplateForm) => {
  return request({
    url: '/system/qaTemplate',
    method: 'post',
    data: data
  });
};

/**
 * 修改问答模版
 * @param data
 */
export const updateQaTemplate = (data: QaTemplateForm) => {
  return request({
    url: '/system/qaTemplate',
    method: 'put',
    data: data
  });
};

/**
 * 删除问答模版
 * @param id
 */
export const delQaTemplate = (id: string | number | Array<string | number>) => {
  return request({
    url: '/system/qaTemplate/' + id,
    method: 'delete'
  });
};
